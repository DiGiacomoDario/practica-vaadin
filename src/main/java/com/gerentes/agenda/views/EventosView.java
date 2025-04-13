package com.gerentes.agenda.views;

import com.gerentes.agenda.model.Evento;
import com.gerentes.agenda.model.EventoEstado;
import com.gerentes.agenda.model.Gerente;
import com.gerentes.agenda.security.SecurityUtils;
import com.gerentes.agenda.service.EventoService;
import com.gerentes.agenda.service.GerenteService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.stereotype.Component;
import org.vaadin.stefan.fullcalendar.BusinessHours;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Vista para la gestión de eventos utilizando un calendario.
 */
@Route(value = "eventos", layout = MainLayout.class)
@PageTitle("Eventos | Agenda de Gerentes")
@PermitAll
@Component
public class EventosView extends VerticalLayout {

    private final EventoService eventoService;
    private final SecurityUtils securityUtils;
    private final GerenteService gerenteService;

    private FullCalendar calendar;
    private HorizontalLayout toolbar;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private final List<Entry> entries = new ArrayList<>();

    public EventosView(EventoService eventoService, SecurityUtils securityUtils, GerenteService gerenteService) {
        this.eventoService = eventoService;
        this.securityUtils = securityUtils;
        this.gerenteService = gerenteService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureCalendar();
        configureToolbar();

        add(toolbar, calendar);

        // Inicializar fechas, pero no cargar eventos aquí
        startDate = LocalDateTime.now().withDayOfMonth(1); // Primer día del mes actual
        endDate = LocalDateTime.now().plusMonths(1); // Un mes después
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // Cargar eventos cuando la vista esté adjunta a la UI
        cargarEventos();
    }

    private void configureCalendar() {
        // Construir el calendario con las opciones deseadas
        calendar = FullCalendarBuilder.create().build();

        // Configurar opciones básicas
        calendar.setLocale(new Locale("es", "ES"));
        calendar.setFirstDay(DayOfWeek.MONDAY);
        calendar.setNowIndicatorShown(true);

        // Configurar horas de negocio (8:00 - 18:00 en días laborables)
        calendar.setBusinessHours(
                new BusinessHours(
                        LocalTime.of(8, 0),
                        LocalTime.of(18, 0),
                        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
                )
        );

        calendar.setSizeFull();
        calendar.changeView(CalendarViewImpl.DAY_GRID_MONTH);

        // Manejar clics en eventos
        calendar.addEntryClickedListener(event -> {
            Entry entry = event.getEntry();
            Long eventoId = entry.getCustomProperty("eventoId");

            if (eventoId != null) {
                eventoService.buscarPorId(eventoId).ifPresent(this::abrirFormulario);
            }
        });

        // Manejar clics en fechas vacías
        calendar.addTimeslotsSelectedListener(event -> {
            LocalDateTime fechaInicio = event.getStart();
            boolean allDay = event.isAllDay();

            Evento evento = new Evento();
            evento.setTitulo("");
            evento.setFechaInicio(fechaInicio);
            evento.setFechaFin(fechaInicio.plusHours(1));
            evento.setEstado(EventoEstado.PENDIENTE);

            if (allDay) {
                evento.setFechaInicio(fechaInicio.withHour(0).withMinute(0));
                evento.setFechaFin(fechaInicio.withHour(23).withMinute(59));
            }

            abrirFormulario(evento);
        });

        // Manejar cambios en el rango de fechas visible
        calendar.addDatesRenderedListener(event -> {
            startDate = event.getStart().atStartOfDay();
            endDate = event.getEnd().atStartOfDay();
            cargarEventos();
        });

        // Manejar eventos de arrastrar y soltar
        calendar.addEntryDroppedListener(event -> {
            Entry entry = event.getEntry();
            updateEventFromEntry(entry);
        });

        // Manejar redimensionamiento de eventos
        calendar.addEntryResizedListener(event -> {
            Entry entry = event.getEntry();
            updateEventFromEntry(entry);
        });
    }

    private void configureToolbar() {
        Button refreshButton = new Button("Actualizar", e -> {
            cargarEventos();
            Notification.show("Calendario actualizado", 2000, Notification.Position.BOTTOM_CENTER);
        });

        Button newEventButton = new Button("Nuevo Evento", e -> {
            LocalDateTime now = LocalDateTime.now();
            Evento evento = new Evento();
            evento.setTitulo("");
            evento.setFechaInicio(now);
            evento.setFechaFin(now.plusHours(1));
            evento.setEstado(EventoEstado.PENDIENTE);
            abrirFormulario(evento);
        });

        newEventButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Select<CalendarViewImpl> viewSelect = new Select<>();
        viewSelect.setItems(CalendarViewImpl.DAY_GRID_MONTH, CalendarViewImpl.TIME_GRID_WEEK, CalendarViewImpl.TIME_GRID_DAY);
        viewSelect.setItemLabelGenerator(view -> {
            return switch (view) {
                case DAY_GRID_MONTH -> "Mes";
                case TIME_GRID_WEEK -> "Semana";
                case TIME_GRID_DAY -> "Día";
                default -> view.getClientSideValue();
            };
        });
        viewSelect.setValue(CalendarViewImpl.DAY_GRID_MONTH);
        viewSelect.addValueChangeListener(e -> calendar.changeView(e.getValue()));

        toolbar = new HorizontalLayout(refreshButton, newEventButton, viewSelect);
        toolbar.setPadding(true);
        toolbar.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        toolbar.setWidthFull();
    }

    private void cargarEventos() {
        entries.clear();

        List<Evento> eventos;
        try {
            if (securityUtils.isAdmin()) {
                eventos = eventoService.listarEventosPorRangoFechas(startDate, endDate);
            } else {
                Optional<Gerente> gerenteOpt = securityUtils.getCurrentGerente();
                if (gerenteOpt.isPresent()) {
                    Gerente gerente = gerenteOpt.get();
                    eventos = eventoService.listarEventosPorGerenteYRangoFechas(gerente, startDate, endDate);
                } else {
                    // No mostrar notificación aquí, ya que puede no haber UI disponible
                    // En su lugar, simplemente no cargar eventos
                    return;
                }
            }

            eventos.forEach(evento -> {
                if (evento.getFechaInicio() != null && evento.getFechaFin() != null) {
                    entries.add(crearEntryDesdeEvento(evento));
                }
            });

            // Actualizar el calendario (forzar recarga manual)
            calendar.gotoDate(startDate.toLocalDate()); // Forzar actualización

        } catch (Exception e) {
            // Mostrar notificación solo si hay una UI disponible
            if (getUI().isPresent()) {
                Notification.show("Error al cargar eventos: " + e.getMessage(), 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }

    private Entry crearEntryDesdeEvento(Evento evento) {
        Entry entry = new Entry();
        entry.setTitle(evento.getTitulo());
        entry.setStart(evento.getFechaInicio());
        entry.setEnd(evento.getFechaFin());
        entry.setDescription(evento.getDescripcion());
        entry.setAllDay(Duration.between(evento.getFechaInicio(), evento.getFechaFin()).toHours() >= 23);
        entry.setColor(getColorForEstado(evento.getEstado()));
        entry.setCustomProperty("eventoId", evento.getId());
        return entry;
    }

    private String getColorForEstado(EventoEstado estado) {
        if (estado == null) {
            return "#3788d8"; // Color por defecto de FullCalendar
        }

        return switch (estado) {
            case PENDIENTE -> "#FFA500"; // Naranja
            case CONFIRMADO -> "#3788d8"; // Azul
            case COMPLETADO -> "#28a745"; // Verde
            case CANCELADO -> "#dc3545"; // Rojo
        };
    }

    private void updateEventFromEntry(Entry entry) {
        Long eventoId = entry.getCustomProperty("eventoId");
        if (eventoId == null) {
            Notification.show("Error: ID de evento no encontrado", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        eventoService.buscarPorId(eventoId).ifPresent(evento -> {
            try {
                evento.setFechaInicio(entry.getStart());
                evento.setFechaFin(entry.getEnd());
                eventoService.actualizarEvento(eventoId, evento);
                cargarEventos(); // Refrescar el calendario
                Notification.show("Evento actualizado", 2000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception e) {
                Notification.show("Error al actualizar evento: " + e.getMessage(), 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
    }

    private void guardarEvento(Evento evento) {
        try {
            if (evento.getId() == null) {
                eventoService.guardarEvento(evento);
                cargarEventos(); // Refrescar el calendario
                Notification.show("Evento creado", 3000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                eventoService.actualizarEvento(evento.getId(), evento);
                cargarEventos(); // Refrescar el calendario
                Notification.show("Evento actualizado", 3000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage(), 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void eliminarEvento(Evento evento) {
        try {
            eventoService.eliminarEvento(evento.getId());
            cargarEventos(); // Refrescar el calendario
            Notification.show("Evento eliminado", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error al eliminar: " + e.getMessage(), 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void confirmarEliminacion(Evento evento) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Confirmar eliminación");
        dialog.setText("¿Está seguro que desea eliminar el evento \"" + evento.getTitulo() + "\"?");

        dialog.setCancelable(true);
        dialog.setCancelText("Cancelar");

        dialog.setConfirmText("Eliminar");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(e -> eliminarEvento(evento));

        dialog.open();
    }

    private void abrirFormulario(Evento evento) {
        EventoFormDialog dialog = new EventoFormDialog(evento, this::guardarEvento);
        dialog.open();
    }

    /**
     * Clase interna para el formulario de edición de eventos.
     */
    private class EventoFormDialog extends Dialog {

        public EventoFormDialog(Evento evento, java.util.function.Consumer<Evento> saveCallback) {
            setWidth("500px");
            setHeaderTitle(evento.getId() == null ? "Nuevo Evento" : "Editar Evento");
            setCloseOnOutsideClick(false);
            setDraggable(true);
            setResizable(true);

            FormLayout formLayout = new FormLayout();
            formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

            TextField tituloField = new TextField("Título");
            tituloField.setValue(evento.getTitulo() != null ? evento.getTitulo() : "");
            tituloField.setWidthFull();
            tituloField.setRequired(true);

            TextArea descripcionField = new TextArea("Descripción");
            descripcionField.setValue(evento.getDescripcion() != null ? evento.getDescripcion() : "");
            descripcionField.setWidthFull();
            descripcionField.setHeight("100px");

            DateTimePicker fechaInicioField = new DateTimePicker("Fecha de inicio");
            fechaInicioField.setValue(evento.getFechaInicio());
            fechaInicioField.setWidthFull();

            DateTimePicker fechaFinField = new DateTimePicker("Fecha de fin");
            fechaFinField.setValue(evento.getFechaFin());
            fechaFinField.setWidthFull();

            fechaInicioField.addValueChangeListener(e -> {
                if (e.getValue() != null && (fechaFinField.getValue() == null || fechaFinField.getValue().isBefore(e.getValue()))) {
                    fechaFinField.setValue(e.getValue().plusHours(1));
                }
            });

            TextField ubicacionField = new TextField("Ubicación");
            ubicacionField.setValue(evento.getUbicacion() != null ? evento.getUbicacion() : "");
            ubicacionField.setWidthFull();

            ComboBox<EventoEstado> estadoField = new ComboBox<>("Estado");
            estadoField.setItems(EventoEstado.values());
            estadoField.setItemLabelGenerator(EventoEstado::getDescripcion);
            estadoField.setValue(evento.getEstado() != null ? evento.getEstado() : EventoEstado.PENDIENTE);
            estadoField.setWidthFull();

            Checkbox notificarField = new Checkbox("Notificar");
            notificarField.setValue(evento.isNotificar());

            NumberField tiempoNotificacionField = new NumberField("Tiempo de notificación (minutos)");
            tiempoNotificacionField.setMin(0);
            tiempoNotificacionField.setValue(evento.getTiempoNotificacion() != null ? evento.getTiempoNotificacion().doubleValue() : 15d);
            tiempoNotificacionField.setStep(5);
            tiempoNotificacionField.setWidthFull();
            tiempoNotificacionField.setVisible(notificarField.getValue());

            notificarField.addValueChangeListener(e -> tiempoNotificacionField.setVisible(e.getValue()));

            ComboBox<Gerente> gerenteField = new ComboBox<>("Gerente");
            if (securityUtils.isAdmin()) {
                // Los administradores pueden asignar eventos a cualquier gerente
                gerenteField.setItems(gerenteService.listarGerentes());
                gerenteField.setItemLabelGenerator(Gerente::getNombreCompleto);
                gerenteField.setValue(evento.getGerente());
                gerenteField.setWidthFull();
            } else {
                // Los gerentes solo pueden crear eventos para sí mismos
                Optional<Gerente> gerenteOpt = securityUtils.getCurrentGerente();
                if (gerenteOpt.isPresent()) {
                    Gerente gerente = gerenteOpt.get();
                    gerenteField.setItems(gerente);
                    gerenteField.setItemLabelGenerator(Gerente::getNombreCompleto);
                    gerenteField.setValue(gerente);
                    gerenteField.setReadOnly(true);
                    gerenteField.setWidthFull();
                }
            }

            formLayout.add(
                    tituloField, descripcionField, fechaInicioField, fechaFinField,
                    ubicacionField, estadoField, notificarField, tiempoNotificacionField, gerenteField
            );

            Div content = new Div(formLayout);
            content.setSizeFull();
            add(content);

            Button cancelButton = new Button("Cancelar", e -> close());

            Button deleteButton = new Button("Eliminar", e -> {
                close();
                if (evento.getId() != null) {
                    confirmarEliminacion(evento);
                }
            });
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteButton.setVisible(evento.getId() != null);

            Button saveButton = new Button("Guardar", e -> {
                if (tituloField.isEmpty()) {
                    Notification.show("El título es obligatorio", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                if (fechaInicioField.isEmpty() || fechaFinField.isEmpty()) {
                    Notification.show("Las fechas son obligatorias", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                if (fechaFinField.getValue().isBefore(fechaInicioField.getValue())) {
                    Notification.show("La fecha de fin debe ser posterior a la fecha de inicio", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                evento.setTitulo(tituloField.getValue());
                evento.setDescripcion(descripcionField.getValue());
                evento.setFechaInicio(fechaInicioField.getValue());
                evento.setFechaFin(fechaFinField.getValue());
                evento.setUbicacion(ubicacionField.getValue());
                evento.setEstado(estadoField.getValue());
                evento.setNotificar(notificarField.getValue());

                // Convertir el valor Double de tiempoNotificacionField a Integer para el evento
                Integer tiempoNotificacion = notificarField.getValue() ?
                        (tiempoNotificacionField.getValue() != null ?
                                tiempoNotificacionField.getValue().intValue() : 15) : null;
                evento.setTiempoNotificacion(tiempoNotificacion);

                evento.setGerente(gerenteField.getValue());

                close();
                if (saveCallback != null) {
                    saveCallback.accept(evento);
                }
            });
            saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            boolean canEdit = evento.getId() == null || securityUtils.isAdmin() ||
                    (evento.getGerente() != null && securityUtils.getCurrentGerente().isPresent() &&
                            Objects.equals(evento.getGerente().getId(), securityUtils.getCurrentGerente().get().getId()));

            if (canEdit) {
                // Solo mostramos el botón de guardar si:
                // - Es un evento nuevo
                // - El usuario es admin
                // - El evento pertenece al gerente actual
                getFooter().add(cancelButton, deleteButton, saveButton);
            } else {
                // Modo solo lectura
                tituloField.setReadOnly(true);
                descripcionField.setReadOnly(true);
                fechaInicioField.setReadOnly(true);
                fechaFinField.setReadOnly(true);
                ubicacionField.setReadOnly(true);
                estadoField.setReadOnly(true);
                notificarField.setReadOnly(true);
                tiempoNotificacionField.setReadOnly(true);
                gerenteField.setReadOnly(true);

                getFooter().add(new Button("Cerrar", e -> close()));
            }
        }
    }
}
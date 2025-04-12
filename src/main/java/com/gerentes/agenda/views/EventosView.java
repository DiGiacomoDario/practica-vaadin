package com.gerentes.agenda.views;

import com.gerentes.agenda.model.Evento;
import com.gerentes.agenda.model.EventoEstado;
import com.gerentes.agenda.model.Gerente;
import com.gerentes.agenda.security.SecurityUtils;
import com.gerentes.agenda.service.EventoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Vista para la gestión de eventos mediante una grilla en lugar de un calendario.
 * Esta es una implementación temporal hasta que se resuelva la integración con FullCalendar.
 */
@Route(value = "eventos", layout = MainLayout.class)
@PageTitle("Eventos | Agenda de Gerentes")
@PermitAll
public class EventosView extends VerticalLayout {

    private final EventoService eventoService;
    private final SecurityUtils securityUtils;
    private Grid<Evento> grid;
    private HorizontalLayout toolbar;

    @Autowired
    public EventosView(EventoService eventoService, SecurityUtils securityUtils) {
        this.eventoService = eventoService;
        this.securityUtils = securityUtils;

        addClassName("eventos-view");
        setSizeFull();
        setPadding(true);

        H2 titulo = new H2("Gestión de Eventos");
        add(titulo);

        configureGrid();
        configureToolbar();
        add(toolbar, grid);
        
        cargarEventos();
    }

    private void configureGrid() {
        grid = new Grid<>(Evento.class);
        grid.addClassName("eventos-grid");
        grid.setSizeFull();
        
        // Configurar columnas
        grid.removeAllColumns();
        
        grid.addColumn(Evento::getTitulo).setHeader("Título").setAutoWidth(true).setSortable(true);
        grid.addColumn(evento -> formatDateTime(evento.getFechaInicio())).setHeader("Fecha inicio").setAutoWidth(true).setSortable(true);
        grid.addColumn(evento -> formatDateTime(evento.getFechaFin())).setHeader("Fecha fin").setAutoWidth(true).setSortable(true);
        grid.addColumn(new ComponentRenderer<>(evento -> {
            Span badge = new Span(evento.getEstado().toString());
            badge.getElement().getThemeList().add("badge");
            
            switch (evento.getEstado()) {
                case PENDIENTE:
                    badge.getElement().getThemeList().add("primary");
                    break;
                case CONFIRMADO:
                    badge.getElement().getThemeList().add("success");
                    break;
                case CANCELADO:
                    badge.getElement().getThemeList().add("error");
                    break;
                case COMPLETADO:
                    badge.getElement().getThemeList().add("contrast");
                    break;
            }
            
            return badge;
        })).setHeader("Estado").setAutoWidth(true);
        
        // Solo mostrar el gerente si el usuario es admin
        if (SecurityUtils.hasRole("ROLE_ADMIN")) {
            grid.addColumn(evento -> evento.getGerente() != null ? evento.getGerente().getNombre() : "").setHeader("Gerente").setAutoWidth(true);
        }
        
        // Columna de acciones
        grid.addComponentColumn(evento -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT), e -> abrirFormulario(evento));
            editButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_SMALL);
            
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH), e -> confirmarEliminacion(evento));
            deleteButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_SMALL, 
                                          com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR);
            
            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.setPadding(false);
            return actions;
        }).setHeader("Acciones").setAutoWidth(true).setFlexGrow(0);
        
        // Evento al hacer clic en una fila
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                abrirFormulario(event.getValue());
                grid.asSingleSelect().clear(); // Limpiar selección para permitir seleccionar la misma fila de nuevo
            }
        });
    }
    
    private void configureToolbar() {
        toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        
        Button addButton = new Button("Nuevo Evento", new Icon(VaadinIcon.PLUS));
        addButton.addClickListener(e -> abrirFormulario(null));
        
        toolbar.add(addButton);
        toolbar.setPadding(true);
    }

    private void cargarEventos() {
        // Obtener los eventos según el rol del usuario
        Optional<Gerente> gerenteOpt = securityUtils.getCurrentGerente();
        
        if (gerenteOpt.isPresent() && !SecurityUtils.hasRole("ROLE_ADMIN")) {
            // Si es un gerente (no admin), mostrar solo sus eventos
            Gerente gerente = gerenteOpt.get();
            List<Evento> eventos = eventoService.listarEventosPorGerente(gerente);
            grid.setItems(eventos);
        } else {
            // Si es admin, mostrar todos los eventos
            List<Evento> eventos = eventoService.listarTodos();
            grid.setItems(eventos);
        }
    }
    
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    
    private void confirmarEliminacion(Evento evento) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Confirmar eliminación");
        
        VerticalLayout content = new VerticalLayout();
        content.add(new Span("¿Está seguro que desea eliminar el evento \"" + evento.getTitulo() + "\"?"));
        content.setSpacing(true);
        content.setPadding(true);
        
        Button cancelButton = new Button("Cancelar", e -> dialog.close());
        Button deleteButton = new Button("Eliminar", e -> {
            eliminarEvento(evento);
            dialog.close();
        });
        deleteButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY, 
                                     com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR);
        
        dialog.getFooter().add(cancelButton, deleteButton);
        dialog.add(content);
        
        dialog.open();
    }
    
    private void eliminarEvento(Evento evento) {
        try {
            eventoService.eliminarEvento(evento.getId());
            cargarEventos(); // Recargar la lista
            Notification.show("Evento eliminado", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error al eliminar: " + e.getMessage(), 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void abrirFormulario(Evento evento) {
        EventoFormDialog dialog = new EventoFormDialog(evento, eventoService, securityUtils, savedEvento -> {
            cargarEventos();  // Recargar eventos después de guardar
            Notification.show(
                    evento == null ? "Evento creado" : "Evento actualizado",
                    3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        
        dialog.open();
    }

    /**
     * Clase interna para el formulario de edición de eventos.
     */
    private class EventoFormDialog extends Dialog {
        
        public EventoFormDialog(Evento evento, EventoService eventoService, SecurityUtils securityUtils,
                               java.util.function.Consumer<Evento> saveCallback) {
            setWidth("500px");
            setHeaderTitle(evento == null ? "Nuevo Evento" : "Editar Evento");

            // Aquí se implementaría un formulario completo
            // Por simplicidad, solo mostramos un mensaje indicando que esta funcionalidad se implementará después
            
            VerticalLayout content = new VerticalLayout(
                new H3("Formulario de Evento"),
                new Span("Esta funcionalidad se implementará más adelante")
            );
            content.setPadding(true);
            content.setSpacing(true);
            add(content);
            
            Button cancelButton = new Button("Cancelar", e -> close());
            Button saveButton = new Button("Guardar", e -> {
                // Aquí iría la lógica de guardado
                close();
                if (saveCallback != null) {
                    saveCallback.accept(evento);
                }
            });
            saveButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY);
            
            getFooter().add(cancelButton, saveButton);
        }
    }
}
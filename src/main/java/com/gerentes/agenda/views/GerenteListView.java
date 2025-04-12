package com.gerentes.agenda.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.gerentes.agenda.model.Gerente;
import com.gerentes.agenda.model.Usuario;
import com.gerentes.agenda.service.GerenteService;
import com.gerentes.agenda.service.UsuarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

/**
 * Vista para la gestión de gerentes (solo para administradores).
 */
@Route(value = "gerentes", layout = MainLayout.class)
@PageTitle("Gestión de Gerentes | Agenda de Gerentes")
@RolesAllowed("ROLE_ADMIN")
public class GerenteListView extends VerticalLayout {

    private final GerenteService gerenteService;
    private final UsuarioService usuarioService;
    private Grid<Gerente> grid;
    private TextField filtroTexto;
    private CallbackDataProvider<Gerente, Void> dataProvider;

    @Autowired
    public GerenteListView(GerenteService gerenteService, UsuarioService usuarioService) {
        this.gerenteService = gerenteService;
        this.usuarioService = usuarioService;

        addClassName("gerentes-list-view");
        setSizeFull();
        setPadding(true);

        configureGrid();
        configureToolbar();

        add(
            new H2("Gestión de Gerentes"),
            getToolbar(),
            grid
        );

        updateDataProvider();
    }

    private void configureGrid() {
        grid = new Grid<>(Gerente.class);
        grid.addClassName("gerentes-grid");
        grid.setSizeFull();
        grid.setColumns("id", "nombre", "email", "telefono", "cargo", "departamento");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        
        // Mejoras visuales para el grid
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        
        // Columna de acciones
        grid.addComponentColumn(gerente -> {
            HorizontalLayout layout = new HorizontalLayout();
            
            Button editButton = new Button(new Icon(VaadinIcon.EDIT), click -> editarGerente(gerente));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH), click -> confirmarEliminacion(gerente));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            
            layout.add(editButton, deleteButton);
            return layout;
        }).setHeader("Acciones").setAutoWidth(true);
    }

    private HorizontalLayout getToolbar() {
        filtroTexto = new TextField();
        filtroTexto.setPlaceholder("Buscar...");
        filtroTexto.setClearButtonVisible(true);
        filtroTexto.setValueChangeMode(ValueChangeMode.LAZY);
        filtroTexto.addValueChangeListener(e -> updateDataProvider());

        Button addButton = new Button("Nuevo Gerente", new Icon(VaadinIcon.PLUS));
        addButton.addClickListener(click -> crearGerente());

        HorizontalLayout toolbar = new HorizontalLayout(filtroTexto, addButton);
        toolbar.addClassName("toolbar");
        toolbar.setWidthFull();
        toolbar.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        
        return toolbar;
    }

    private void configureToolbar() {
        // Configuración adicional de la barra de herramientas si es necesario
    }

    private void updateDataProvider() {
        String filterText = filtroTexto.getValue().trim();
        
        dataProvider = DataProvider.fromCallbacks(
            // Primer callback: obtener items para la página actual
            query -> {
                int offset = query.getOffset();
                int limit = query.getLimit();
                
                Sort sort = Sort.by(Sort.Direction.ASC, "nombre");
                if (!query.getSortOrders().isEmpty()) {
                    sort = Sort.by(
                        query.getSortOrders().get(0).getDirection() == com.vaadin.flow.data.provider.SortDirection.ASCENDING 
                            ? Sort.Direction.ASC 
                            : Sort.Direction.DESC,
                        query.getSortOrders().get(0).getSorted()
                    );
                }
                
                Pageable pageable = PageRequest.of(offset / limit, limit, sort);
                Page<Gerente> page;
                
                if (filterText.isEmpty()) {
                    page = gerenteService.listarTodosPaginado(pageable);
                } else {
                    page = gerenteService.buscarPorNombreContienePaginado(filterText, pageable);
                }
                
                return page.getContent().stream();
            },
            // Segundo callback: contar el total de items
            query -> {
                if (filterText.isEmpty()) {
                    return (int) gerenteService.listarTodos().size();
                } else {
                    return (int) gerenteService.buscarPorNombreContiene(filterText).size();
                }
            }
        );
        
        grid.setDataProvider(dataProvider);
    }

    private void crearGerente() {
        Gerente nuevoGerente = new Gerente();
        GerenteFormDialog dialog = new GerenteFormDialog(nuevoGerente, true);
        dialog.open();
    }

    private void editarGerente(Gerente gerente) {
        GerenteFormDialog dialog = new GerenteFormDialog(gerente, false);
        dialog.open();
    }

    private void confirmarEliminacion(Gerente gerente) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Confirmar eliminación");
        dialog.setText("¿Está seguro que desea eliminar al gerente " + gerente.getNombre() + "? Esta acción no se puede deshacer.");
        
        dialog.setCancelable(true);
        dialog.setCancelText("Cancelar");
        
        dialog.setConfirmText("Eliminar");
        dialog.setConfirmButtonTheme("error primary");
        
        dialog.addConfirmListener(event -> eliminarGerente(gerente));
        
        dialog.open();
    }

    private void eliminarGerente(Gerente gerente) {
        try {
            gerenteService.eliminarGerente(gerente.getId());
            updateDataProvider();
            Notification.show("Gerente eliminado", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error al eliminar: " + e.getMessage(), 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    /**
     * Clase interna para el formulario de creación y edición de gerentes.
     */
    private class GerenteFormDialog extends Dialog {
        private final Gerente gerente;
        private final boolean esNuevo;
        private final Binder<Gerente> binder = new BeanValidationBinder<>(Gerente.class);
        
        private final TextField nombre = new TextField("Nombre");
        private final EmailField email = new EmailField("Email");
        private final TextField telefono = new TextField("Teléfono");
        private final TextField cargo = new TextField("Cargo");
        private final TextField departamento = new TextField("Departamento");
        
        // Campos para usuario (solo se muestran en creación)
        private final TextField username = new TextField("Usuario");
        private final PasswordField password = new PasswordField("Contraseña");
        
        public GerenteFormDialog(Gerente gerente, boolean esNuevo) {
            this.gerente = gerente;
            this.esNuevo = esNuevo;
            
            configureDialog();
            configureForm();
            configureBinder();
            
            binder.readBean(this.gerente);
        }
        
        private void configureDialog() {
            setWidth("600px");
            setHeaderTitle(esNuevo ? "Nuevo Gerente" : "Editar Gerente");
            
            Button cancelButton = new Button("Cancelar", e -> close());
            Button saveButton = new Button("Guardar", e -> guardarGerente());
            saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            
            getFooter().add(cancelButton, saveButton);
            setCloseOnEsc(true);
            setCloseOnOutsideClick(false);
        }
        
        private void configureForm() {
            nombre.setRequired(true);
            email.setRequired(true);
            
            FormLayout formLayout = new FormLayout();
            formLayout.add(nombre, email, telefono, cargo, departamento);
            formLayout.setResponsiveSteps(
                    new FormLayout.ResponsiveStep("0", 1),
                    new FormLayout.ResponsiveStep("500px", 2)
            );
            
            VerticalLayout mainLayout = new VerticalLayout(formLayout);
            mainLayout.setPadding(false);
            
            // Si es un nuevo gerente, mostrar campos para crear usuario
            if (esNuevo) {
                H3 usuarioTitle = new H3("Cuenta de Usuario");
                username.setRequired(true);
                password.setRequired(true);
                
                FormLayout userFormLayout = new FormLayout(username, password);
                userFormLayout.setResponsiveSteps(
                        new FormLayout.ResponsiveStep("0", 1),
                        new FormLayout.ResponsiveStep("500px", 2)
                );
                
                mainLayout.add(usuarioTitle, userFormLayout);
            }
            
            add(mainLayout);
        }
        
        private void configureBinder() {
            binder.bindInstanceFields(this);
            
            // Validaciones adicionales
            email.addValueChangeListener(event -> {
                if (event.getValue() != null && !event.getValue().isEmpty() && esNuevo) {
                    // Verificar si el email ya está registrado
                    if (gerenteService.buscarPorEmail(event.getValue()).isPresent()) {
                        email.setErrorMessage("Este email ya está registrado");
                        email.setInvalid(true);
                    }
                }
            });
            
            if (esNuevo) {
                username.addValueChangeListener(event -> {
                    if (event.getValue() != null && !event.getValue().isEmpty()) {
                        // Verificar si el username ya está registrado
                        if (usuarioService.existeUsername(event.getValue())) {
                            username.setErrorMessage("Este nombre de usuario ya está en uso");
                            username.setInvalid(true);
                        }
                    }
                });
            }
        }
        
        private void guardarGerente() {
            try {
                binder.writeBean(gerente);
                
                if (esNuevo) {
                    // Guardar el nuevo gerente
                    Gerente gerenteGuardado = gerenteService.guardarGerente(gerente);
                    
                    // Crear usuario asociado
                    if (gerenteGuardado != null && !username.isEmpty() && !password.isEmpty()) {
                        Usuario usuario = usuarioService.crearUsuarioGerente(
                                gerenteGuardado, 
                                username.getValue(), 
                                password.getValue()
                        );
                        
                        if (usuario == null) {
                            Notification.show("Error al crear el usuario", 3000, Notification.Position.BOTTOM_CENTER)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        }
                    }
                    
                    Notification.show("Gerente creado correctamente", 3000, Notification.Position.BOTTOM_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } else {
                    // Actualizar un gerente existente
                    gerenteService.actualizarGerente(gerente.getId(), gerente);
                    Notification.show("Gerente actualizado correctamente", 3000, Notification.Position.BOTTOM_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                }
                
                close();
                updateDataProvider();
                
            } catch (ValidationException e) {
                Notification.show("Hay errores en el formulario", 3000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (Exception e) {
                Notification.show("Error al guardar: " + e.getMessage(), 3000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }
}
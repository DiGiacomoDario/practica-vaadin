package com.gerentes.agenda.views;

import com.gerentes.agenda.model.Gerente;
import com.gerentes.agenda.model.Usuario;
import com.gerentes.agenda.service.GerenteService;
import com.gerentes.agenda.service.UsuarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

        actualizarLista();
    }

    private void configureGrid() {
        grid = new Grid<>(Gerente.class);
        grid.addClassName("gerentes-grid");
        grid.setSizeFull();
        grid.setColumns("id", "nombre", "email", "telefono", "cargo", "departamento");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        
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
        filtroTexto.addValueChangeListener(e -> actualizarLista());

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

    private void actualizarLista() {
        List<Gerente> gerentes;
        if (filtroTexto.isEmpty()) {
            gerentes = gerenteService.listarTodos();
        } else {
            gerentes = gerenteService.buscarPorNombreContiene(filtroTexto.getValue());
        }
        grid.setItems(gerentes);
    }

    private void crearGerente() {
        // Por simplicidad, este método solo mostrará una notificación
        // En una implementación real, aquí se abriría un formulario de creación de gerentes
        Notification.show("La creación de gerentes se implementará en la siguiente fase", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }

    private void editarGerente(Gerente gerente) {
        // Por simplicidad, este método solo mostrará una notificación
        // En una implementación real, aquí se abriría un formulario de edición
        Notification.show("La edición de gerentes se implementará en la siguiente fase", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_PRIMARY);
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
            actualizarLista();
            Notification.show("Gerente eliminado", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error al eliminar: " + e.getMessage(), 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
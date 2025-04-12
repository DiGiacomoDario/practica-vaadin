package com.gerentes.agenda.views;

import com.gerentes.agenda.model.Gerente;
import com.gerentes.agenda.model.Usuario;
import com.gerentes.agenda.security.SecurityUtils;
import com.gerentes.agenda.service.GerenteService;
import com.gerentes.agenda.service.UsuarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * Vista para que los usuarios puedan ver y editar su información personal.
 */
@Route(value = "perfil", layout = MainLayout.class)
@PageTitle("Mi Perfil | Agenda de Gerentes")
@PermitAll
public class PerfilView extends VerticalLayout {

    private final SecurityUtils securityUtils;
    private final UsuarioService usuarioService;
    private final GerenteService gerenteService;
    
    private final TextField nombre = new TextField("Nombre");
    private final EmailField email = new EmailField("Email");
    private final TextField telefono = new TextField("Teléfono");
    private final TextField cargo = new TextField("Cargo");
    private final TextField departamento = new TextField("Departamento");
    
    private final TextField username = new TextField("Nombre de usuario");
    private final PasswordField passwordActual = new PasswordField("Contraseña actual");
    private final PasswordField passwordNueva = new PasswordField("Nueva contraseña");
    private final PasswordField passwordConfirmacion = new PasswordField("Confirmar contraseña");
    
    private final Binder<Gerente> gerenteBinder = new Binder<>(Gerente.class);
    
    private Usuario usuarioActual;
    private Gerente gerenteActual;

    @Autowired
    public PerfilView(SecurityUtils securityUtils, UsuarioService usuarioService, GerenteService gerenteService) {
        this.securityUtils = securityUtils;
        this.usuarioService = usuarioService;
        this.gerenteService = gerenteService;

        addClassName("perfil-view");
        setSizeFull();
        setPadding(true);

        add(new H2("Mi Perfil"));
        
        cargarUsuario();
    }

    private void cargarUsuario() {
        Optional<Usuario> usuario = securityUtils.getCurrentUser();
        
        if (usuario.isPresent()) {
            this.usuarioActual = usuario.get();
            
            // Configurar campos de usuario
            username.setValue(usuarioActual.getUsername());
            username.setReadOnly(true);
            
            // Configurar sección de cambio de contraseña
            VerticalLayout credencialesLayout = crearSeccionCredenciales();
            
            if (securityUtils.getCurrentGerente().isPresent()) {
                // Es un gerente, configurar vista de gerente
                this.gerenteActual = securityUtils.getCurrentGerente().get();
                VerticalLayout datosPersonalesLayout = crearSeccionDatosPersonales();
                
                add(datosPersonalesLayout, credencialesLayout);
            } else {
                // Es un administrador sin gerente asociado
                add(new Paragraph("Cuenta de administrador"), credencialesLayout);
            }
        } else {
            add(new Paragraph("No se ha podido cargar la información del usuario. Intente iniciar sesión nuevamente."));
        }
    }

    private VerticalLayout crearSeccionDatosPersonales() {
        H3 datosPersonalesTitle = new H3("Datos Personales");
        datosPersonalesTitle.addClassName(LumoUtility.Margin.Top.MEDIUM);
        
        // Configurar binder
        gerenteBinder.setBean(gerenteActual);
        gerenteBinder.bind(nombre, "nombre");
        gerenteBinder.bind(email, "email");
        gerenteBinder.bind(telefono, "telefono");
        gerenteBinder.bind(cargo, "cargo");
        gerenteBinder.bind(departamento, "departamento");
        
        // Configurar form layout
        FormLayout formLayout = new FormLayout();
        formLayout.add(nombre, email, telefono, cargo, departamento);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        
        // Botones
        Button guardarButton = new Button("Guardar cambios");
        guardarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        guardarButton.addClickListener(e -> guardarDatosPersonales());
        
        Button cancelarButton = new Button("Cancelar");
        cancelarButton.addClickListener(e -> gerenteBinder.readBean(gerenteActual));
        
        HorizontalLayout buttonLayout = new HorizontalLayout(guardarButton, cancelarButton);
        buttonLayout.setWidthFull();
        buttonLayout.setPadding(true);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        
        VerticalLayout layout = new VerticalLayout(datosPersonalesTitle, formLayout, buttonLayout);
        layout.setPadding(false);
        
        return layout;
    }

    private VerticalLayout crearSeccionCredenciales() {
        H3 credencialesTitle = new H3("Credenciales de acceso");
        credencialesTitle.addClassName(LumoUtility.Margin.Top.MEDIUM);
        
        // Configurar form layout
        FormLayout formLayout = new FormLayout();
        formLayout.add(username, passwordActual, passwordNueva, passwordConfirmacion);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        
        // Botones
        Button cambiarButton = new Button("Cambiar contraseña");
        cambiarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cambiarButton.addClickListener(e -> cambiarContrasena());
        
        Button limpiarButton = new Button("Limpiar");
        limpiarButton.addClickListener(e -> {
            passwordActual.clear();
            passwordNueva.clear();
            passwordConfirmacion.clear();
        });
        
        HorizontalLayout buttonLayout = new HorizontalLayout(cambiarButton, limpiarButton);
        buttonLayout.setWidthFull();
        buttonLayout.setPadding(true);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        
        VerticalLayout layout = new VerticalLayout(credencialesTitle, formLayout, buttonLayout);
        layout.setPadding(false);
        
        return layout;
    }

    private void guardarDatosPersonales() {
        try {
            // Validar datos
            gerenteBinder.writeBean(gerenteActual);
            
            // Guardar cambios
            gerenteService.actualizarGerente(gerenteActual.getId(), gerenteActual);
            
            Notification.show("Datos personales actualizados correctamente", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    
        } catch (ValidationException e) {
            Notification.show("Hay errores en el formulario", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            Notification.show("Error al guardar: " + e.getMessage(), 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void cambiarContrasena() {
        // Para este ejemplo, simplemente mostraremos un mensaje
        // En una implementación real, verificaríamos la contraseña actual y actualizaríamos la nueva
        
        if (passwordNueva.isEmpty() || passwordConfirmacion.isEmpty() || passwordActual.isEmpty()) {
            Notification.show("Debe completar todos los campos", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        
        if (!passwordNueva.getValue().equals(passwordConfirmacion.getValue())) {
            Notification.show("Las contraseñas no coinciden", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        
        // En una aplicación real, aquí iría la verificación de la contraseña actual y 
        // el cambio de contraseña usando un servicio
        Notification.show("La funcionalidad de cambio de contraseña será implementada en la siguiente fase", 
                3000, Notification.Position.BOTTOM_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                
        passwordActual.clear();
        passwordNueva.clear();
        passwordConfirmacion.clear();
    }
}
package com.gerentes.agenda.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Vista de inicio de sesión para la aplicación.
 */
@Route("login")
@PageTitle("Login | Agenda de Gerentes")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");
        
        // Configuración del título y descripción
        H1 title = new H1("Agenda de Gerentes");
        title.addClassNames(
            LumoUtility.Margin.NONE,
            LumoUtility.FontSize.XXLARGE,
            LumoUtility.FontWeight.BOLD
        );
        
        // Agregar un logo o imagen si está disponible
        // Image logo = new Image("images/logo.png", "Logo");
        // logo.setWidth("150px");
        
        // Configuración de textos personalizados en el formulario de login
        login.setForgotPasswordButtonVisible(false);
        login.setI18n(createSpanishI18n());

        // Agregar componentes a la vista
        add(
            // logo,
            title,
            login
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // Comprueba si hay un parámetro de error en la URL
        if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
    
    /**
     * Crea un objeto de internacionalización en español para el formulario de login.
     */
    private com.vaadin.flow.component.login.LoginI18n createSpanishI18n() {
        final com.vaadin.flow.component.login.LoginI18n i18n = com.vaadin.flow.component.login.LoginI18n.createDefault();
        
        i18n.setHeader(new com.vaadin.flow.component.login.LoginI18n.Header());
        i18n.getHeader().setTitle("Acceso al Sistema");
        i18n.getHeader().setDescription("Inicie sesión con sus credenciales");
        
        i18n.setForm(new com.vaadin.flow.component.login.LoginI18n.Form());
        i18n.getForm().setUsername("Usuario");
        i18n.getForm().setPassword("Contraseña");
        i18n.getForm().setSubmit("Iniciar sesión");
        i18n.getForm().setTitle("Iniciar sesión");
        
        i18n.setErrorMessage(new com.vaadin.flow.component.login.LoginI18n.ErrorMessage());
        i18n.getErrorMessage().setTitle("Usuario/contraseña incorrectos");
        i18n.getErrorMessage().setMessage("Verifique sus credenciales e intente nuevamente.");
        
        return i18n;
    }
}
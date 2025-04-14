package com.gerentes.agenda.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Route("login")
@PageTitle("Login | Agenda de Gerentes")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private static final Logger logger = LoggerFactory.getLogger(LoginView.class);
    private final LoginForm login = new LoginForm();

    public LoginView() {
        try {
            logger.info("Initializing LoginView");
            addClassName("login-view");
            setSizeFull();
            setAlignItems(Alignment.CENTER);
            setJustifyContentMode(JustifyContentMode.CENTER);

            // Configure login form
            login.setAction("login");
            login.setForgotPasswordButtonVisible(false);
            login.setI18n(createSpanishI18n());

            // Log login attempts
            login.addLoginListener(event -> {
                logger.info("Login attempted for username: {}", event.getUsername());
            });

            // Title
            H1 title = new H1("Agenda de Gerentes");
            title.addClassNames(
                    LumoUtility.Margin.NONE,
                    LumoUtility.FontSize.XXLARGE,
                    LumoUtility.FontWeight.BOLD,
                    LumoUtility.TextColor.HEADER
            );

            // Add components
            add(title, login);

            logger.info("LoginView components added");
        } catch (Exception e) {
            logger.error("Error initializing LoginView", e);
            Notification.show("Error al cargar la página de inicio de sesión", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        logger.info("BeforeEnter: Query parameters = {}", beforeEnterEvent.getLocation().getQueryParameters().getParameters());
        if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
            Notification.show("Usuario o contraseña incorrectos", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private com.vaadin.flow.component.login.LoginI18n createSpanishI18n() {
        final com.vaadin.flow.component.login.LoginI18n i18n = com.vaadin.flow.component.login.LoginI18n.createDefault();

        i18n.setHeader(new com.vaadin.flow.component.login.LoginI18n.Header());
        i18n.getHeader().setTitle("Acceso al Sistema");
        i18n.getHeader().setDescription("Inicie sesión con sus credenciales");

        i18n.setForm(new com.vaadin.flow.component.login.LoginI18n.Form());
        i18n.getForm().setUsername("Usuario");
        i18n.getForm().setPassword("Contraseña");
        i18n.getForm().setSubmit("Iniciar sesión");
        i18n.getForm().setTitle("");

        i18n.setErrorMessage(new com.vaadin.flow.component.login.LoginI18n.ErrorMessage());
        i18n.getErrorMessage().setTitle("Usuario/contraseña incorrectos");
        i18n.getErrorMessage().setMessage("Verifique sus credenciales e intente nuevamente.");

        return i18n;
    }
}
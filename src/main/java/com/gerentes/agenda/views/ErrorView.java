package com.gerentes.agenda.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("error-view")
@AnonymousAllowed
public class ErrorView extends VerticalLayout implements HasErrorParameter<Exception> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(ErrorView.class);
    
    public ErrorView() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        getStyle().set("text-align", "center");
        
        Icon icon = VaadinIcon.WARNING.create();
        icon.setSize("50px");
        icon.setColor("var(--lumo-error-color)");
        
        H1 header = new H1("¡Ups! Algo salió mal");
        H3 subHeader = new H3("Ha ocurrido un error en la aplicación");
        
        Paragraph message = new Paragraph("Estamos trabajando para resolver el problema. " +
                "Por favor, intenta acceder nuevamente o contacta al administrador.");
        
        Button loginButton = new Button("Volver al inicio de sesión", click -> {
            UI.getCurrent().navigate("login");
        });
        loginButton.getStyle().set("margin-top", "20px");
        
        add(icon, header, subHeader, message, loginButton);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter) {
        Exception exception = parameter.getException();
        logger.error("Error no manejado en la navegación: {}", exception.getMessage(), exception);
        
        return 500; // Código HTTP para errores internos del servidor
    }
}
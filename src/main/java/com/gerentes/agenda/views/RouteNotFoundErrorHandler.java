package com.gerentes.agenda.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manejador personalizado para errores de ruta no encontrada (404)
 */
@Route("not-found-error")
@AnonymousAllowed
public class RouteNotFoundErrorHandler extends VerticalLayout implements HasErrorParameter<NotFoundException> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(RouteNotFoundErrorHandler.class);

    public RouteNotFoundErrorHandler() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        getStyle().set("text-align", "center");
        getStyle().set("padding", "2rem");
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        logger.warn("Ruta no encontrada: {}", event.getLocation().getPath());
        
        // Limpiar el contenido
        removeAll();
        
        // Contenido
        Icon icon = VaadinIcon.EXCLAMATION_CIRCLE_O.create();
        icon.setSize("50px");
        icon.setColor("var(--lumo-primary-color)");
        
        H1 header = new H1("Página no encontrada");
        header.getStyle().set("margin-top", "1rem");
        
        Paragraph message = new Paragraph("La ruta que buscas no está disponible. Por favor, verifica la URL o regresa al inicio.");
        
        Button loginButton = new Button("Ir al inicio de sesión", click -> {
            UI.getCurrent().navigate("login");
        });
        loginButton.getStyle().set("margin-top", "1rem");
        
        add(icon, header, message, loginButton);
        
        return 404; // Código HTTP para Not Found
    }
}
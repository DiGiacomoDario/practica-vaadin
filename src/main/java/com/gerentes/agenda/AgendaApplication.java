package com.gerentes.agenda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.shared.ui.Transport;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Clase principal de la aplicación.
 * Configura Spring Boot y Vaadin Flow con soporte para comunicación en tiempo real.
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
@Theme(themeClass = Lumo.class, variant = Lumo.LIGHT)
@Push(value = PushMode.AUTOMATIC, transport = Transport.WEBSOCKET)
public class AgendaApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(AgendaApplication.class, args);
    }
}
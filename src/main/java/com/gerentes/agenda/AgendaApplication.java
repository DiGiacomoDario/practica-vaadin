package com.gerentes.agenda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@SpringBootApplication
@EnableScheduling
@Theme(themeClass = Lumo.class, variant = Lumo.LIGHT)
public class AgendaApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(AgendaApplication.class, args);
    }
}
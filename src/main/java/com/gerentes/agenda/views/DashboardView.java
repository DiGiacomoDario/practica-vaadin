package com.gerentes.agenda.views;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.gerentes.agenda.model.Gerente;
import com.gerentes.agenda.security.SecurityUtils;
import com.gerentes.agenda.service.EventoService;
import com.gerentes.agenda.service.GerenteService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

/**
 * Vista principal del Dashboard que muestra información relevante según el rol del usuario.
 */
@Route(value = "dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | Agenda de Gerentes")
@PermitAll
public class DashboardView extends VerticalLayout {

    private final SecurityUtils securityUtils;
    private final GerenteService gerenteService;
    private final EventoService eventoService;

    public DashboardView(SecurityUtils securityUtils, GerenteService gerenteService, EventoService eventoService) {
        this.securityUtils = securityUtils;
        this.gerenteService = gerenteService;
        this.eventoService = eventoService;
        
        addClassName("dashboard-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        
        H2 title = new H2("Dashboard");
        title.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.Margin.Bottom.MEDIUM);
        
        add(title);
        
        createDashboardContent();
    }

    private void createDashboardContent() {
        // Verificamos si es un administrador o un gerente
        if (SecurityUtils.hasRole("ROLE_ADMIN")) {
            createAdminDashboard();
        } else {
            createGerenteDashboard();
        }
    }

    private void createAdminDashboard() {
        // Tarjetas de resumen para administradores
        HorizontalLayout summaryCards = new HorizontalLayout();
        summaryCards.setWidthFull();
        summaryCards.setPadding(true);
        summaryCards.setSpacing(true);
        
        // Total de gerentes
        long totalGerentes = gerenteService.listarTodos().size();
        summaryCards.add(createSummaryCard("Gerentes", String.valueOf(totalGerentes), VaadinIcon.USERS, "var(--lumo-primary-color)"));
        
        // Total de eventos este mes
        long eventosDelMes = eventoService.contarEventosMes();
        summaryCards.add(createSummaryCard("Eventos este mes", String.valueOf(eventosDelMes), VaadinIcon.CALENDAR, "var(--lumo-success-color)"));
        
        // Fecha y hora actual
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        Paragraph lastUpdate = new Paragraph("Última actualización: " + formattedDate);
        lastUpdate.addClassNames(LumoUtility.TextAlignment.CENTER, LumoUtility.FontSize.SMALL);
        
        add(summaryCards, lastUpdate);
        
        // Mensaje de bienvenida
        H3 welcomeTitle = new H3("Bienvenido, Administrador");
        welcomeTitle.addClassNames(LumoUtility.Margin.Top.LARGE);
        
        Paragraph welcomeText = new Paragraph("Desde este panel puede gestionar a los gerentes y monitorear la actividad del sistema.");
        
        add(welcomeTitle, welcomeText);
    }

    private void createGerenteDashboard() {
        Optional<Gerente> gerenteActual = securityUtils.getCurrentGerente();
        
        if (gerenteActual.isPresent()) {
            Gerente gerente = gerenteActual.get();
            
            // Tarjetas de resumen para gerentes
            HorizontalLayout summaryCards = new HorizontalLayout();
            summaryCards.setWidthFull();
            summaryCards.setPadding(true);
            summaryCards.setSpacing(true);
            
            // Eventos próximos (7 días)
            long eventosProximos = eventoService.contarEventosProximosDias(gerente, 7);
            summaryCards.add(createSummaryCard("Próximos 7 días", String.valueOf(eventosProximos), VaadinIcon.CALENDAR_CLOCK, "var(--lumo-primary-color)"));
            
            // Total eventos este mes
            long eventosMes = eventoService.contarEventosMesGerente(gerente);
            summaryCards.add(createSummaryCard("Eventos este mes", String.valueOf(eventosMes), VaadinIcon.CALENDAR, "var(--lumo-success-color)"));
            
            // Fecha y hora actual
            LocalDateTime now = LocalDateTime.now();
            String formattedDate = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            
            Paragraph lastUpdate = new Paragraph("Última actualización: " + formattedDate);
            lastUpdate.addClassNames(LumoUtility.TextAlignment.CENTER, LumoUtility.FontSize.SMALL);
            
            add(summaryCards, lastUpdate);
            
            // Mensaje de bienvenida
            H3 welcomeTitle = new H3("Bienvenido, " + gerente.getNombre());
            welcomeTitle.addClassNames(LumoUtility.Margin.Top.LARGE);
            
            Paragraph welcomeText = new Paragraph("Desde este panel puede gestionar sus eventos y revisar su agenda.");
            
            add(welcomeTitle, welcomeText);
        } else {
            // Caso en que no se encuentre un gerente asociado
            add(new Paragraph("No se ha encontrado información de gerente asociada a su usuario."));
        }
    }

    private VerticalLayout createSummaryCard(String title, String value, VaadinIcon iconType, String iconColor) {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("summary-card");
        card.setWidth("250px");
        card.setHeight("150px");
        card.setPadding(true);
        card.setSpacing(false);
        card.getStyle().set("background-color", "var(--lumo-base-color)")
                        .set("border-radius", "var(--lumo-border-radius-m)")
                        .set("box-shadow", "var(--lumo-box-shadow-xs)");
        
        // Icono
        Icon icon = new Icon(iconType);
        icon.setSize("40px");
        icon.setColor(iconColor);
        
        // Título y valor
        H3 valueText = new H3(value);
        valueText.addClassName(LumoUtility.FontSize.XXLARGE);
        valueText.getStyle().set("margin", "0");
        
        Span titleSpan = new Span(title);
        titleSpan.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);
        
        card.add(icon, valueText, titleSpan);
        card.setHorizontalComponentAlignment(Alignment.CENTER, icon, valueText, titleSpan);
        
        return card;
    }
}
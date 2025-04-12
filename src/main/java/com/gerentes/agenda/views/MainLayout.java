package com.gerentes.agenda.views;

import com.gerentes.agenda.security.SecurityUtils;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Layout principal de la aplicación que incluye el menú de navegación y opciones de usuario.
 */
public class MainLayout extends AppLayout {

    private final AccessAnnotationChecker accessChecker;

    public MainLayout(AccessAnnotationChecker accessChecker) {
        this.accessChecker = accessChecker;

        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Agenda de Gerentes");
        logo.addClassNames(
            LumoUtility.FontSize.LARGE,
            LumoUtility.Margin.MEDIUM
        );

        String username = SecurityUtils.getCurrentUsername()
                .orElse("Usuario");

        Button logout = new Button("Cerrar sesión", e -> {
            SecurityContextHolder.clearContext();
            getUI().ifPresent(ui -> ui.navigate("login"));
        });
        logout.setIcon(new Icon(VaadinIcon.SIGN_OUT));

        Span userGreeting = new Span("Hola, " + username);
        userGreeting.addClassNames(LumoUtility.Margin.Right.MEDIUM);

        HorizontalLayout userActions = new HorizontalLayout(userGreeting, logout);
        userActions.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        HorizontalLayout header = new HorizontalLayout(
            new DrawerToggle(),
            logo
        );
        header.setWidthFull();
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.add(userActions);

        addToNavbar(header);
    }

    private void createDrawer() {
        SideNav nav = new SideNav();

        // Agregar elementos de navegación según rol
        // Todos los usuarios autenticados pueden ver el dashboard
        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, new Icon(VaadinIcon.DASHBOARD)));
        
        // Todos los usuarios pueden ver el calendario de eventos
        nav.addItem(new SideNavItem("Eventos", EventosView.class, new Icon(VaadinIcon.CALENDAR)));

        // Solo usuarios con rol ADMIN pueden gestionar gerentes
        if (SecurityUtils.hasRole("ROLE_ADMIN")) {
            nav.addItem(new SideNavItem("Gestión de Gerentes", GerenteListView.class, new Icon(VaadinIcon.USERS)));
        }

        // Todos los usuarios pueden ver su perfil
        nav.addItem(new SideNavItem("Mi Perfil", PerfilView.class, new Icon(VaadinIcon.USER)));

        addToDrawer(nav);
    }
}
package com.gerentes.agenda.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.gerentes.agenda.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

/**
 * Configuración de seguridad de la aplicación.
 */
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfiguration(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Configura las rutas públicas y las que requieren autenticación
        http.authorizeHttpRequests(auth -> {
            // PASO 1: Define los recursos públicos primero
            auth.requestMatchers(
                    new AntPathRequestMatcher("/VAADIN/**"),           // Recursos de Vaadin
                    new AntPathRequestMatcher("/frontend/**"),         // Recursos frontend de Vaadin
                    new AntPathRequestMatcher("/icons/**"),            // Iconos
                    new AntPathRequestMatcher("/images/**"),           // Imágenes
                    new AntPathRequestMatcher("/h2-console/**"),       // Consola H2
                    new AntPathRequestMatcher("/manifest.webmanifest"),// Manifest para PWA
                    new AntPathRequestMatcher("/sw.js"),               // Service worker
                    new AntPathRequestMatcher("/offline.html"),        // Página offline
                    new AntPathRequestMatcher("/login")               // Ruta de login
            ).permitAll();

            // PASO 2: Define las rutas para administradores
            auth.requestMatchers(new AntPathRequestMatcher("/gerentes/**"))
                    .hasAuthority("ROLE_ADMIN");

            // PASO 3: Por último, establece que todo lo demás requiere autenticación
            auth.anyRequest().authenticated();
        });

        // Configuración para permitir la consola H2 en desarrollo
        http.csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")));
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // Configuración de la vista de login usando Vaadin
        setLoginView(http, LoginView.class);
    }
}
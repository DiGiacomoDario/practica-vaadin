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
        // Call the parent configuration first to set up Vaadin security
        super.configure(http);
        
        // Configura las rutas públicas y las que requieren autenticación
        http.authorizeHttpRequests(auth -> {
            // PASO 1: Define los recursos públicos primero
            auth.requestMatchers(
                    // Recursos estáticos de Vaadin
                    new AntPathRequestMatcher("/VAADIN/**"),
                    new AntPathRequestMatcher("/icons/**"),
                    new AntPathRequestMatcher("/images/**"),
                    // Consola H2 para desarrollo
                    new AntPathRequestMatcher("/h2-console/**"),
                    // Otros recursos públicos
                    new AntPathRequestMatcher("/manifest.webmanifest"),
                    new AntPathRequestMatcher("/sw.js"),
                    new AntPathRequestMatcher("/offline.html")
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
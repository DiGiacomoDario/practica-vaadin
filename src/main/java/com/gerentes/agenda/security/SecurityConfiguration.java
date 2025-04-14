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
        // Reglas de autorización
        http.authorizeHttpRequests(auth -> {
            // Recursos públicos
            auth.requestMatchers(
                    // Recursos de Vaadin
                    new AntPathRequestMatcher("/VAADIN/**"),
                    new AntPathRequestMatcher("/vaadinServlet/**"),
                    new AntPathRequestMatcher("/PUSH/**"),
                    new AntPathRequestMatcher("/UIDL/**"),
                    new AntPathRequestMatcher("/HEARTBEAT/**"),
                    new AntPathRequestMatcher("/VAADIN/push/**"),
                    
                    // Recursos estáticos
                    new AntPathRequestMatcher("/favicon.ico"),
                    new AntPathRequestMatcher("/robots.txt"),
                    new AntPathRequestMatcher("/frontend/**"),
                    new AntPathRequestMatcher("/frontend-es6/**"),
                    new AntPathRequestMatcher("/frontend-es5/**"),
                    new AntPathRequestMatcher("/styles/**"),
                    new AntPathRequestMatcher("/icons/**"),
                    new AntPathRequestMatcher("/images/**"),
                    
                    // Base de datos
                    new AntPathRequestMatcher("/h2-console/**"),
                    
                    // PWA
                    new AntPathRequestMatcher("/manifest.webmanifest"),
                    new AntPathRequestMatcher("/sw.js"),
                    new AntPathRequestMatcher("/sw-runtime-resources-precache.js"),
                    new AntPathRequestMatcher("/offline.html"),
                    new AntPathRequestMatcher("/offline-stub.html"),
                    
                    // Autenticación y errores
                    new AntPathRequestMatcher("/login"),
                    new AntPathRequestMatcher("/login/**"),
                    new AntPathRequestMatcher("/error/**")
            ).permitAll();

            // Páginas protegidas
            auth.requestMatchers(new AntPathRequestMatcher("/gerentes/**"))
                    .hasAuthority("ROLE_ADMIN");

            // Todo lo demás requiere autenticación
            auth.anyRequest().authenticated();
        });

        // Deshabilitar CSRF para Vaadin push y consola H2
        http.csrf(csrf -> csrf.ignoringRequestMatchers(
                new AntPathRequestMatcher("/VAADIN/**"),
                new AntPathRequestMatcher("/VAADIN/push/**"),
                new AntPathRequestMatcher("/h2-console/**")
        ));

        // Permitir H2 console en frames
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // Configurar el login
        http.formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
        );

        // Usar la vista de login de Vaadin
        setLoginView(http, LoginView.class);
    }
}
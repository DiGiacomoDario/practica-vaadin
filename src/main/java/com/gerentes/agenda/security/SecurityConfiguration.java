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
                    new AntPathRequestMatcher("/login"),
                    new AntPathRequestMatcher("/error"), // Allow /error
                    new AntPathRequestMatcher("/h2-console/**"),
                    new AntPathRequestMatcher("/VAADIN/**"),
                    new AntPathRequestMatcher("/frontend/**"),
                    new AntPathRequestMatcher("/styles/**"),
                    new AntPathRequestMatcher("/icons/**"),
                    new AntPathRequestMatcher("/images/**"),
                    new AntPathRequestMatcher("/offline.html"),
                    new AntPathRequestMatcher("/offline-stub.html")
            ).permitAll();

            // Páginas protegidas
            auth.requestMatchers(new AntPathRequestMatcher("/gerentes/**"))
                    .hasAuthority("ROLE_ADMIN");

            // Todo lo demás requiere autenticación
            auth.anyRequest().authenticated();
        });

        // Deshabilitar CSRF solo para Vaadin y H2 console
        http.csrf(csrf -> csrf.ignoringRequestMatchers(
                new AntPathRequestMatcher("/VAADIN/**"),
                new AntPathRequestMatcher("/h2-console/**")
        ));

        // Permitir H2 console en frames
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // Configurar el login
        http.formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
        );

        // Usar la vista de login de Vaadin
        setLoginView(http, LoginView.class);
    }
}
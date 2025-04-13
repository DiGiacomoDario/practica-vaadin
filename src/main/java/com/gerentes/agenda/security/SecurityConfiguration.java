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
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(
                    new AntPathRequestMatcher("/VAADIN/**"),
                    new AntPathRequestMatcher("/VAADIN/push/**"),
                    new AntPathRequestMatcher("/frontend/**"),
                    new AntPathRequestMatcher("/styles/**"), // Add for CSS
                    new AntPathRequestMatcher("/icons/**"),
                    new AntPathRequestMatcher("/images/**"),
                    new AntPathRequestMatcher("/h2-console/**"),
                    new AntPathRequestMatcher("/manifest.webmanifest"),
                    new AntPathRequestMatcher("/sw.js"),
                    new AntPathRequestMatcher("/offline.html"),
                    new AntPathRequestMatcher("/offline-stub.html"), // Add this
                    new AntPathRequestMatcher("/login"),
                    new AntPathRequestMatcher("/login/**"),
                    new AntPathRequestMatcher("/error/**")
            ).permitAll();

            auth.requestMatchers(new AntPathRequestMatcher("/gerentes/**"))
                    .hasAuthority("ROLE_ADMIN");

            auth.anyRequest().authenticated();
        });

        // Disable CSRF for Vaadin push and H2 console
        http.csrf(csrf -> csrf.ignoringRequestMatchers(
                new AntPathRequestMatcher("/VAADIN/**"),
                new AntPathRequestMatcher("/VAADIN/push/**"),
                new AntPathRequestMatcher("/h2-console/**")
        ));

        // Allow H2 console in frames
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // Configure login
        http.formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
        );

        // Use Vaadin's login view
        setLoginView(http, LoginView.class);
    }
}
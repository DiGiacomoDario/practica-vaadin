package com.gerentes.agenda.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * Configuración para los recursos PWA y el manejo de la funcionalidad offline
 */
@Configuration
public class PwaConfiguration implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Asegurar que los recursos PWA estén disponibles
        registry.addResourceHandler("/manifest.webmanifest", "/sw.js", "/offline.html", "/icons/**")
                .addResourceLocations("classpath:/META-INF/resources/");
        
        // Redirigir favicon.ico
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/META-INF/resources/icons/favicon.ico")
                .setCachePeriod(0)
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource resource = location.createRelative(resourcePath);
                        return resource.exists() && resource.isReadable() ? resource : 
                               new ClassPathResource("/META-INF/resources/icons/favicon.ico");
                    }
                });
    }
    
    /**
     * Clase interna para manejar intercepción de solicitudes cuando está offline
     */
    private static class OfflineInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            // Nunca bloquear recursos específicos incluso en modo offline
            String requestURI = request.getRequestURI();
            if (requestURI.startsWith("/VAADIN/") || 
                requestURI.equals("/manifest.webmanifest") ||
                requestURI.equals("/sw.js") ||
                requestURI.equals("/offline.html") ||
                requestURI.equals("/favicon.ico") ||
                requestURI.startsWith("/icons/")) {
                return true;
            }
            
            return true; // Permitir todas las solicitudes por defecto
        }
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new OfflineInterceptor());
    }
}
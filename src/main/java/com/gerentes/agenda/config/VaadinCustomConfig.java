package com.gerentes.agenda.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

/**
 * Configuración personalizada para el servidor Vaadin
 * Mejora el manejo de errores y sesiones para prevenir problemas de conexión
 */
@Configuration
public class VaadinCustomConfig implements VaadinServiceInitListener {
    
    private static final Logger logger = LoggerFactory.getLogger(VaadinCustomConfig.class);

    @Override
    public void serviceInit(ServiceInitEvent event) {
        logger.info("Inicializando configuración personalizada de Vaadin");
        
        // Configurar timeout para carga de recursos
        // Configurar timeout para carga de recursos
        event.getSource().getDeploymentConfiguration().getInitParameters().put("loadTimeout", "60000");
        // Configurar manejadores de errores
        event.getSource().addSessionInitListener(sessionInit -> {
            logger.info("Nueva sesión Vaadin iniciada: {}", sessionInit.getSession().getSession().getId());
            
            // Configurar el error handler para que no redirija en caso de errores
            sessionInit.getSession().setErrorHandler(errorEvent -> {
                logger.error("Error en sesión Vaadin: {}", errorEvent.getThrowable().getMessage(), errorEvent.getThrowable());
            });
        });
        
        // Configurar manejo de desconexión
        event.getSource().addUIInitListener(uiInit -> {
            uiInit.getUI().getPage().addJavaScript(
                    "window.addEventListener('unload', function() { " +
                    "  if (Vaadin.Flow && Vaadin.Flow.clients) { " +
                    "    Object.values(Vaadin.Flow.clients).forEach(client => { " +
                    "      if (client.isActive()) { " +
                    "        client.forceDisconnect(); " +
                    "      } " +
                    "    }); " +
                    "  } " +
                    "});"
            );
        });
        
        // Aumentar los límites de memoria para carga de archivos
        // Aumentar los límites de memoria para carga de archivos
        // Set max request size using system property
        System.setProperty("vaadin.maxMessageSize", String.valueOf(50 * 1024 * 1024)); // 50 MB
}
}
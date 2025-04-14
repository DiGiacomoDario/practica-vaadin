package com.gerentes.agenda.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración personalizada para el servidor Tomcat
 * Optimiza los parámetros para mejor rendimiento con WebSockets y Vaadin
 */
@Configuration
public class TomcatWebServerConfig {
    
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return (factory) -> {
            // Aumentar el timeout de la conexión para evitar desconexiones prematuras
            factory.addConnectorCustomizers(connector -> {
                connector.setProperty("connectionTimeout", "20000");
                connector.setProperty("maxKeepAliveRequests", "100");
                connector.setProperty("keepAliveTimeout", "30000");
                connector.setProperty("maxConnections", "10000");
                connector.setProperty("acceptCount", "100");
                connector.setProperty("maxThreads", "200");
                
                // Configuración específica para WebSockets
                connector.setProperty("socket.appReadBufSize", "16384");
                connector.setProperty("socket.appWriteBufSize", "16384");
                connector.setProperty("socket.txBufSize", "64000");
                connector.setProperty("socket.rxBufSize", "64000");
            });
            
            // Configurar parámetros adicionales para la sesión y el contexto
            factory.addContextCustomizers(context -> {
                context.setSessionTimeout(120); // 120 minutos
                context.setSwallowAbortedUploads(true);
                // The method setClearReferencesThreadLocals was removed in later Tomcat versions.
                // The default behavior is typically sufficient.
            });
        };
    }
}
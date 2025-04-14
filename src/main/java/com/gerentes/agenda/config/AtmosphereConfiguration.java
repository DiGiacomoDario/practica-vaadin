//package com.gerentes.agenda.config;
//
//import org.atmosphere.cpr.ApplicationConfig;
//import org.atmosphere.cpr.AtmosphereServlet;
//import org.springframework.boot.web.servlet.ServletRegistrationBean; // Correct import
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Configuración personalizada para Atmosphere (WebSockets)
// * Esta clase mejora la gestión de conexiones para evitar problemas de "Connection lost"
// */
//@Configuration
//public class AtmosphereConfiguration {
//
//    @Bean
//    public ServletRegistrationBean<AtmosphereServlet> atmosphereServlet() {
//        ServletRegistrationBean<AtmosphereServlet> registration = new ServletRegistrationBean<>(
//                new AtmosphereServlet(), "/VAADIN/push/*");
//
//        // Extender el timeout de la conexión
//        registration.addInitParameter(ApplicationConfig.HEARTBEAT_INTERVAL_IN_SECONDS, "15");
//        // registration.addInitParameter(ApplicationConfig.HEARTBEAT_PADDING, "padding"); // Removed: Constant does not exist
//        registration.addInitParameter(ApplicationConfig.SUSPENDED_ATMOSPHERE_RESOURCE_UUID, "false");
//        registration.addInitParameter(ApplicationConfig.RECOVER_DEAD_BROADCASTER, "true");
//        registration.addInitParameter(ApplicationConfig.MAX_INACTIVE, "300000"); // 5 minutos
//        registration.addInitParameter(ApplicationConfig.BROADCASTER_LIFECYCLE_POLICY, "NEVER");
//        registration.addInitParameter(ApplicationConfig.BROADCASTER_CACHE, "org.atmosphere.cache.UUIDBroadcasterCache");
//        registration.addInitParameter(ApplicationConfig.WEBSOCKET_MAXTEXTSIZE, "64000");
//        registration.addInitParameter(ApplicationConfig.WEBSOCKET_MAXBINARYSIZE, "64000");
//        registration.addInitParameter(ApplicationConfig.DROP_ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "false");
//        registration.addInitParameter(ApplicationConfig.PROPERTY_SESSION_SUPPORT, "true");
//        registration.addInitParameter(ApplicationConfig.PROPERTY_ALLOW_SESSION_TIMEOUT_REMOVAL, "false");
//        // registration.addInitParameter(ApplicationConfig.PROPERTY_PRESERVE_HOST, "true"); // Removed: Constant does not exist
//        registration.addInitParameter(ApplicationConfig.DISABLE_ATMOSPHEREINTERCEPTOR, "false");
//        registration.addInitParameter(ApplicationConfig.BROADCASTER_SHARABLE_THREAD_POOLS, "true");
//        // registration.addInitParameter(ApplicationConfig.TRACK_MESSAGE_SIZE, "true"); // Removed: Constant does not exist
//
//        // Definir la carga de servicio
//        registration.setLoadOnStartup(1);
//        registration.setAsyncSupported(true);
//
//        return registration;
//    }
//}
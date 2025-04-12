package com.gerentes.agenda.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gerentes.agenda.service.EventoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Componente que programa tareas para el envío de notificaciones.
 */
@Component
public class NotificadorScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificadorScheduler.class);
    
    private final EventoService eventoService;
    
    @Autowired
    public NotificadorScheduler(EventoService eventoService) {
        this.eventoService = eventoService;
    }
    
    /**
     * Planifica el proceso de notificaciones cada minuto.
     * Revisa los eventos que requieren notificación y envía los correos correspondientes.
     */
    @Scheduled(fixedRate = 60000) // Cada minuto (60000 ms)
    public void verificarYNotificarEventos() {
        logger.info("Ejecutando tarea programada para verificar y notificar eventos...");
        try {
            eventoService.procesarNotificaciones();
        } catch (Exception e) {
            logger.error("Error al procesar notificaciones", e);
        }
    }
}
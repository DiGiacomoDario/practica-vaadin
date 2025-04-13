package com.gerentes.agenda.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un Evento en la agenda de los gerentes.
 */
@Entity
@Table(name = "eventos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;
    
    private String descripcion;
    
    @Column(nullable = false)
    private LocalDateTime fechaInicio;
    
    @Column(nullable = false)
    private LocalDateTime fechaFin;
    
    private String ubicacion;
    
    // Indica si se debe enviar notificación por correo
    private boolean notificar;
    
    // Tiempo de anticipación para notificación (en minutos)
    private Integer tiempoNotificacion;
    
    // Estado del evento (PENDIENTE, CONFIRMADO, CANCELADO, COMPLETADO)
    @Enumerated(EnumType.STRING)
    private EventoEstado estado = EventoEstado.PENDIENTE;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gerente_id", nullable = false)
    private Gerente gerente;
    
    // Color para mostrar en el calendario (hexadecimal)
    private String color;
    
    // Método auxiliar para verificar si se debe notificar ahora
    public boolean debeNotificarAhora(LocalDateTime ahora) {
        if (!notificar || tiempoNotificacion == null || tiempoNotificacion < 0 ||
                estado == EventoEstado.CANCELADO || estado == EventoEstado.COMPLETADO) {
            return false;
        }
        LocalDateTime tiempoNotificacion = fechaInicio.minusMinutes(this.tiempoNotificacion);
        LocalDateTime finVentana = fechaInicio.plusMinutes(1); // 1-minute buffer
        return !ahora.isBefore(tiempoNotificacion) && ahora.isBefore(finVentana);
    }

    public void setTiempoNotificacion(Integer tiempoNotificacion) {
        this.tiempoNotificacion = (tiempoNotificacion != null && tiempoNotificacion >= 0) ? tiempoNotificacion : null;
    }
}
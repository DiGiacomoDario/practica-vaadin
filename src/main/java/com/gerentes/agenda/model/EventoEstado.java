package com.gerentes.agenda.model;

/**
 * Enumera los posibles estados de un evento
 */
public enum EventoEstado {
    PENDIENTE("Pendiente"), 
    CONFIRMADO("Confirmado"), 
    CANCELADO("Cancelado"), 
    COMPLETADO("Completado");
    
    private final String descripcion;
    
    EventoEstado(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
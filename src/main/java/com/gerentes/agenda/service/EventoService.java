package com.gerentes.agenda.service;

import com.gerentes.agenda.model.Evento;
import com.gerentes.agenda.model.EventoEstado;
import com.gerentes.agenda.model.Gerente;
import com.gerentes.agenda.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para manejar operaciones relacionadas con Eventos.
 */
@Service
@Transactional
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EmailService emailService;

    @Autowired
    public EventoService(EventoRepository eventoRepository, EmailService emailService) {
        this.eventoRepository = eventoRepository;
        this.emailService = emailService;
    }

    /**
     * Guarda un evento en la base de datos.
     * 
     * @param evento El evento a guardar
     * @return El evento guardado con su ID asignado
     */
    public Evento guardarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    /**
     * Actualiza un evento existente.
     * 
     * @param id ID del evento a actualizar
     * @param eventoActualizado Datos actualizados del evento
     * @return El evento actualizado o null si no se encuentra
     */
    public Evento actualizarEvento(Long id, Evento eventoActualizado) {
        return eventoRepository.findById(id)
                .map(eventoExistente -> {
                    eventoActualizado.setId(id);
                    return eventoRepository.save(eventoActualizado);
                })
                .orElse(null);
    }

    /**
     * Busca un evento por su ID.
     * 
     * @param id ID del evento a buscar
     * @return Optional con el evento si existe, o vacío si no existe
     */
    public Optional<Evento> buscarPorId(Long id) {
        return eventoRepository.findById(id);
    }

    /**
     * Lista todos los eventos.
     * 
     * @return Lista de todos los eventos
     */
    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    /**
     * Lista eventos con paginación.
     * 
     * @param pageable Información de paginación
     * @return Página de eventos
     */
    public Page<Evento> listarTodosPaginado(Pageable pageable) {
        return eventoRepository.findAll(pageable);
    }

    /**
     * Lista todos los eventos de un gerente específico.
     * 
     * @param gerente El gerente cuyos eventos se buscan
     * @return Lista de eventos asociados al gerente
     */
    public List<Evento> listarEventosPorGerente(Gerente gerente) {
        return eventoRepository.findByGerente(gerente);
    }

    /**
     * Lista los eventos de un gerente en un rango de fechas.
     * 
     * @param gerente El gerente cuyos eventos se buscan
     * @param inicio Fecha de inicio del rango
     * @param fin Fecha de fin del rango
     * @return Lista de eventos que ocurren en el rango especificado
     */
    public List<Evento> listarEventosPorGerenteYRangoFechas(Gerente gerente, LocalDateTime inicio, LocalDateTime fin) {
        return eventoRepository.findByGerenteAndRangoFechas(gerente, inicio, fin);
    }

    /**
     * Elimina un evento por su ID.
     * 
     * @param id ID del evento a eliminar
     * @return true si se eliminó, false si no existía
     */
    public boolean eliminarEvento(Long id) {
        return eventoRepository.findById(id)
                .map(evento -> {
                    eventoRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Cambia el estado de un evento.
     * 
     * @param id ID del evento
     * @param estado Nuevo estado para el evento
     * @return El evento actualizado o null si no se encuentra
     */
    public Evento cambiarEstadoEvento(Long id, EventoEstado estado) {
        return eventoRepository.findById(id)
                .map(evento -> {
                    evento.setEstado(estado);
                    return eventoRepository.save(evento);
                })
                .orElse(null);
    }

    /**
     * Busca eventos que necesitan notificación y envía las notificaciones correspondientes.
     * Este método está pensado para ser ejecutado periódicamente por un scheduler.
     */
    public void procesarNotificaciones() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Evento> eventosParaNotificar = eventoRepository.findEventosParaNotificar(ahora);
        
        for (Evento evento : eventosParaNotificar) {
            if (evento.isNotificar() && evento.getGerente() != null && evento.getGerente().getEmail() != null) {
                String destinatario = evento.getGerente().getEmail();
                String asunto = "Recordatorio: " + evento.getTitulo();
                String cuerpo = construirCuerpoEmail(evento);
                
                emailService.enviarEmail(destinatario, asunto, cuerpo);
            }
        }
    }
    
    /**
     * Construye el cuerpo del email de notificación.
     * 
     * @param evento El evento para el cual se construye la notificación
     * @return El cuerpo del email formateado
     */
    private String construirCuerpoEmail(Evento evento) {
        StringBuilder sb = new StringBuilder();
        sb.append("Estimado/a ").append(evento.getGerente().getNombre()).append(",\n\n");
        sb.append("Le recordamos que tiene un evento próximo:\n\n");
        sb.append("Título: ").append(evento.getTitulo()).append("\n");
        sb.append("Descripción: ").append(evento.getDescripcion() != null ? evento.getDescripcion() : "No disponible").append("\n");
        sb.append("Fecha y hora de inicio: ").append(evento.getFechaInicio()).append("\n");
        sb.append("Fecha y hora de finalización: ").append(evento.getFechaFin()).append("\n");
        
        if (evento.getUbicacion() != null && !evento.getUbicacion().isEmpty()) {
            sb.append("Ubicación: ").append(evento.getUbicacion()).append("\n");
        }
        
        sb.append("\nSaludos cordiales,\n");
        sb.append("Sistema de Agenda de Gerentes");
        
        return sb.toString();
    }
}
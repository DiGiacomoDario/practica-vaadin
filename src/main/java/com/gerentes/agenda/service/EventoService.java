package com.gerentes.agenda.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gerentes.agenda.model.Evento;
import com.gerentes.agenda.model.EventoEstado;
import com.gerentes.agenda.model.Gerente;
import com.gerentes.agenda.repository.EventoRepository;

/**
 * Servicio para manejar operaciones relacionadas con Eventos.
 */
@Service
@Transactional
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(EventoService.class);

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
        List<Evento> eventos = eventoRepository.findAll().stream()
                .filter(e -> e.isNotificar() && e.getFechaInicio().isAfter(ahora))
                .filter(e -> {
                    LocalDateTime limite = ahora.plusMinutes(e.getTiempoNotificacion());
                    return e.getFechaInicio().isBefore(limite) || e.getFechaInicio().isEqual(limite);
                })
                .collect(Collectors.toList());

        log.info("Found {} events to notify: {}", eventos.size(), eventos); // Add logging
        for (Evento evento : eventos) {
            if (evento.getGerente() != null && evento.getGerente().getEmail() != null) {
                String destinatario = evento.getGerente().getEmail();
                String asunto = "Recordatorio: " + evento.getTitulo();
                String cuerpo = construirCuerpoEmail(evento);
                log.info("Sending email to {} for event: {}", destinatario, evento.getTitulo());
                emailService.enviarEmail(destinatario, asunto, cuerpo);
            } else {
                log.warn("Skipping notification for event {}: No valid gerente or email", evento.getTitulo());
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

    /**
     * Cuenta el total de eventos registrados en el mes actual.
     * 
     * @return Cantidad de eventos en el mes actual
     */
    public long contarEventosMes() {
        LocalDateTime inicioMes = LocalDateTime.of(
                LocalDate.now().withDayOfMonth(1), 
                LocalTime.MIN);
        
        LocalDateTime finMes = LocalDateTime.of(
                YearMonth.now().atEndOfMonth(), 
                LocalTime.MAX);
        
        return eventoRepository.findAll().stream()
                .filter(evento -> !evento.getFechaInicio().isBefore(inicioMes) && !evento.getFechaInicio().isAfter(finMes))
                .count();
    }
    
    /**
     * Cuenta los eventos que ocurrirán en los próximos días para un gerente específico.
     * 
     * @param gerente El gerente cuyos eventos se contarán
     * @param dias Cantidad de días a considerar
     * @return Número de eventos en los próximos días
     */
    public long contarEventosProximosDias(Gerente gerente, int dias) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusDays(dias);
        
        return eventoRepository.findByGerente(gerente).stream()
                .filter(evento -> !evento.getFechaInicio().isBefore(ahora) && !evento.getFechaInicio().isAfter(limite))
                .count();
    }
    
    /**
     * Cuenta los eventos del mes actual para un gerente específico.
     * 
     * @param gerente El gerente cuyos eventos se contarán
     * @return Número de eventos en el mes actual
     */
    public long contarEventosMesGerente(Gerente gerente) {
        LocalDateTime inicioMes = LocalDateTime.of(
                LocalDate.now().withDayOfMonth(1), 
                LocalTime.MIN);
        
        LocalDateTime finMes = LocalDateTime.of(
                YearMonth.now().atEndOfMonth(), 
                LocalTime.MAX);
        
        return eventoRepository.findByGerente(gerente).stream()
                .filter(evento -> !evento.getFechaInicio().isBefore(inicioMes) && !evento.getFechaInicio().isAfter(finMes))
                .count();
    }
    
    /**
     * Lista todos los eventos en un rango de fechas.
     * 
     * @param inicio Fecha de inicio del rango
     * @param fin Fecha de fin del rango
     * @return Lista de eventos que ocurren en el rango especificado
     */
    public List<Evento> listarEventosPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return eventoRepository.findAll().stream()
                .filter(evento -> 
                    // El evento comienza durante el rango
                    (!evento.getFechaInicio().isBefore(inicio) && !evento.getFechaInicio().isAfter(fin)) ||
                    // El evento termina durante el rango
                    (!evento.getFechaFin().isBefore(inicio) && !evento.getFechaFin().isAfter(fin)) ||
                    // El evento comienza antes y termina después del rango (lo abarca)
                    (evento.getFechaInicio().isBefore(inicio) && evento.getFechaFin().isAfter(fin))
                )
                .collect(Collectors.toList());
    }
}
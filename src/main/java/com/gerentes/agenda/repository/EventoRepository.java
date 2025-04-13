package com.gerentes.agenda.repository;

import com.gerentes.agenda.model.Evento;
import com.gerentes.agenda.model.EventoEstado;
import com.gerentes.agenda.model.Gerente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para gestionar las operaciones CRUD de la entidad Evento.
 */
@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    
    /**
     * Busca eventos de un gerente específico.
     * 
     * @param gerente El gerente cuyos eventos se buscan
     * @return Lista de eventos asociados al gerente
     */
    List<Evento> findByGerente(Gerente gerente);
    
    /**
     * Busca eventos de un gerente que ocurren entre dos fechas.
     * 
     * @param gerente El gerente cuyos eventos se buscan
     * @param inicio Fecha de inicio del rango
     * @param fin Fecha de fin del rango
     * @return Lista de eventos que ocurren en el rango especificado
     */
    @Query("SELECT e FROM Evento e WHERE e.gerente = :gerente AND " +
           "((e.fechaInicio BETWEEN :inicio AND :fin) OR " +
           "(e.fechaFin BETWEEN :inicio AND :fin) OR " +
           "(e.fechaInicio <= :inicio AND e.fechaFin >= :fin))")
    List<Evento> findByGerenteAndRangoFechas(
            @Param("gerente") Gerente gerente, 
            @Param("inicio") LocalDateTime inicio, 
            @Param("fin") LocalDateTime fin);
    
    /**
     * Busca eventos por título (búsqueda parcial insensible a mayúsculas/minúsculas).
     * 
     * @param titulo Parte del título a buscar
     * @return Lista de eventos que coinciden con el criterio de búsqueda
     */
    List<Evento> findByTituloContainingIgnoreCase(String titulo);
    
    /**
     * Busca eventos por estado.
     * 
     * @param estado El estado de los eventos a buscar
     * @return Lista de eventos con el estado especificado
     */
    List<Evento> findByEstado(EventoEstado estado);
    
    /**
     * Busca eventos que deben ser notificados en este momento.
     * 
     * @param ahora Fecha y hora actual
     * @return Lista de eventos que deben ser notificados
     */
    @Query("SELECT e FROM Evento e WHERE e.notificar = true AND e.fechaInicio > :ahora AND e.fechaInicio <= :limite")
    List<Evento> findEventosParaNotificar(@Param("ahora") LocalDateTime ahora, @Param("limite") LocalDateTime limite);
}
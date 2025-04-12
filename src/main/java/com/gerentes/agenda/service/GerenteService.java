package com.gerentes.agenda.service;

import com.gerentes.agenda.model.Gerente;
import com.gerentes.agenda.repository.GerenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para manejar operaciones relacionadas con los Gerentes.
 */
@Service
@Transactional
public class GerenteService {

    private final GerenteRepository gerenteRepository;

    @Autowired
    public GerenteService(GerenteRepository gerenteRepository) {
        this.gerenteRepository = gerenteRepository;
    }

    /**
     * Guarda un gerente en la base de datos.
     * 
     * @param gerente El gerente a guardar
     * @return El gerente guardado con su ID asignado
     */
    public Gerente guardarGerente(Gerente gerente) {
        return gerenteRepository.save(gerente);
    }

    /**
     * Actualiza un gerente existente.
     * 
     * @param id ID del gerente a actualizar
     * @param gerenteActualizado Datos actualizados del gerente
     * @return El gerente actualizado o null si no se encuentra
     */
    public Gerente actualizarGerente(Long id, Gerente gerenteActualizado) {
        if (gerenteRepository.existsById(id)) {
            gerenteActualizado.setId(id);
            return gerenteRepository.save(gerenteActualizado);
        }
        return null;
    }

    /**
     * Busca un gerente por su ID.
     * 
     * @param id ID del gerente a buscar
     * @return Optional con el gerente si existe, o vacío si no existe
     */
    public Optional<Gerente> buscarPorId(Long id) {
        return gerenteRepository.findById(id);
    }

    /**
     * Busca un gerente por su email.
     * 
     * @param email Email del gerente a buscar
     * @return Optional con el gerente si existe, o vacío si no existe
     */
    public Optional<Gerente> buscarPorEmail(String email) {
        return gerenteRepository.findByEmail(email);
    }

    /**
     * Lista todos los gerentes.
     * 
     * @return Lista de todos los gerentes
     */
    public List<Gerente> listarTodos() {
        return gerenteRepository.findAll();
    }

    /**
     * Lista gerentes con paginación.
     * 
     * @param pageable Información de paginación
     * @return Página de gerentes
     */
    public Page<Gerente> listarTodosPaginado(Pageable pageable) {
        return gerenteRepository.findAll(pageable);
    }

    /**
     * Elimina un gerente por su ID.
     * 
     * @param id ID del gerente a eliminar
     * @return true si se eliminó, false si no existía
     */
    public boolean eliminarGerente(Long id) {
        if (gerenteRepository.existsById(id)) {
            gerenteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Busca gerentes cuyo nombre contiene la cadena proporcionada.
     * 
     * @param nombre Parte del nombre a buscar
     * @return Lista de gerentes que coinciden con el criterio
     */
    public List<Gerente> buscarPorNombreContiene(String nombre) {
        return gerenteRepository.findByNombreContainingIgnoreCase(nombre);
    }
}
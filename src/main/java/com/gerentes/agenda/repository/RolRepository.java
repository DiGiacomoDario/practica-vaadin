package com.gerentes.agenda.repository;

import com.gerentes.agenda.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para gestionar las operaciones CRUD de la entidad Rol.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    
    /**
     * Busca un rol por su nombre.
     * 
     * @param nombre El nombre del rol a buscar
     * @return Optional con el rol si existe, vacío si no existe
     */
    Optional<Rol> findByNombre(String nombre);
    
    /**
     * Verifica si existe un rol con el nombre especificado.
     * 
     * @param nombre El nombre del rol a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByNombre(String nombre);
}
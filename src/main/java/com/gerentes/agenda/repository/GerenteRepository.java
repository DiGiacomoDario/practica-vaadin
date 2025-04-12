package com.gerentes.agenda.repository;

import com.gerentes.agenda.model.Gerente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para gestionar las operaciones CRUD de la entidad Gerente.
 */
@Repository
public interface GerenteRepository extends JpaRepository<Gerente, Long> {
    
    /**
     * Busca un gerente por su dirección de correo electrónico.
     * 
     * @param email El correo electrónico del gerente
     * @return Un Optional con el gerente si existe, o vacío si no existe
     */
    Optional<Gerente> findByEmail(String email);
    
    /**
     * Verifica si existe un gerente con el correo electrónico especificado.
     * 
     * @param email El correo electrónico a verificar
     * @return true si existe un gerente con ese correo, false en caso contrario
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca gerentes cuyo nombre contenga la cadena de texto especificada (búsqueda insensible a mayúsculas/minúsculas).
     * 
     * @param nombre Parte del nombre a buscar
     * @return Lista de gerentes que coinciden con el criterio de búsqueda
     */
    java.util.List<Gerente> findByNombreContainingIgnoreCase(String nombre);
}
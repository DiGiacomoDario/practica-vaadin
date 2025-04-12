package com.gerentes.agenda.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa a un Gerente en el sistema.
 */
@Entity
@Table(name = "gerentes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gerente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    private String telefono;
    
    private String cargo;
    
    private String departamento;

    @OneToMany(mappedBy = "gerente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evento> eventos = new ArrayList<>();
    
    /**
     * Obtiene el nombre completo del gerente con información adicional.
     * @return Nombre y cargo del gerente
     */
    public String getNombreCompleto() {
        if (cargo != null && !cargo.isEmpty()) {
            return nombre + " (" + cargo + ")";
        }
        return nombre;
    }
}
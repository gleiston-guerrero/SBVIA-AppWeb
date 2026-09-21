package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity that defines a scenario difficulty level.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "nivel_dificultad")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class DifficultyLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nivel_dificultad")
    private Integer idNivelDificultad;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "valor", nullable = false)
    private Integer value;

    @Column(name = "descripcion", length = 255)
    private String description;
}

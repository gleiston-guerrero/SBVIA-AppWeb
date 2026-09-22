package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity that defines a scenario difficulty level.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "difficulty_level")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class DifficultyLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idNivelDificultad;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "level_value", nullable = false)
    private Integer value;

    @Column(name = "description", length = 255)
    private String description;
}

package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity that describes an AI model available to the simulator.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "ai_model")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class AiModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idModeloIa;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "version", nullable = false, length = 50)
    private String version;

    @Column(name = "model_type", nullable = false, length = 100)
    private String tipoModelo;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "trained_at")
    private java.time.Instant fechaEntrenamiento;

    @Column(name = "model_accuracy", precision = 5, scale = 2)
    private java.math.BigDecimal precisionModelo;

    @Column(name = "parameters")
    private String parametros;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean activo = true;
}

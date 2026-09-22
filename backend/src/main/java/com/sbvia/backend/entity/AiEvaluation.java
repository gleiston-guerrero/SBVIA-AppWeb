package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA entity that stores an evaluation produced by an AI model.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "ai_evaluation")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class AiEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idEvaluacionIa;

    @Column(name = "result", nullable = false, length = 500)
    private String resultado;

    @Column(name = "predicted_class", length = 100)
    private String clasificacionPredicha;

    @Column(name = "confidence_level", precision = 5, scale = 2)
    private BigDecimal nivelConfianza;

    @Column(name = "recommendation", length = 500)
    private String recomendacion;

    @Column(name = "input_data")
    private String datosEntrada;

    @Column(name = "evaluated_at", nullable = false)
    @Builder.Default
    private Instant fechaEvaluacion = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_model_id", nullable = false)
    private AiModel aiModel;
}

package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * JPA entity that groups the simulations belonging to a training session.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "training_session")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idSesion;

    @Column(name = "started_at", nullable = false)
    @Builder.Default
    private Instant fechaInicio = Instant.now();

    @Column(name = "ended_at")
    private Instant endDate;

    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private String estado = "ABIERTA";

    @Column(name = "objective", length = 500)
    private String objetivo;

    @Column(name = "notes", length = 500)
    private String observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

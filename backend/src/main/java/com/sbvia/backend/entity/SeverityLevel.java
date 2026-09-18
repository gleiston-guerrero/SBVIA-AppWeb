package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <p>SeverityLevel class.</p>
 *
 * @author Keitho_
 */
@Entity
@Table(name = "nivel_gravedad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeverityLevel {
    /** Default constructor for SeverityLevel. */
    public SeverityLevel() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nivel_gravedad")
    private Integer idNivelGravedad;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "valor", nullable = false)
    private Integer value;

    @Column(name = "descripcion", length = 255)
    private String description;

    @Column(name = "multiplicador_penalizacion", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private java.math.BigDecimal multiplicadorPenalizacion = java.math.BigDecimal.ONE;
}

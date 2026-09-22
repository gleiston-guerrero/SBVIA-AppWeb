package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity that defines a weather condition that a scenario can use.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "weather_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idTipoClima;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "visibility_factor", precision = 3, scale = 2)
    private java.math.BigDecimal factorVisibilidad;

    @Column(name = "grip_factor", precision = 3, scale = 2)
    private java.math.BigDecimal factorAdherencia;
}

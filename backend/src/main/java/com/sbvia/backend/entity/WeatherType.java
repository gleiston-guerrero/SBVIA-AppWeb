package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity that defines a weather condition that a scenario can use.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "tipo_clima")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_clima")
    private Integer idTipoClima;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "descripcion", length = 255)
    private String description;

    @Column(name = "factor_visibilidad", precision = 3, scale = 2)
    private java.math.BigDecimal factorVisibilidad;

    @Column(name = "factor_adherencia", precision = 3, scale = 2)
    private java.math.BigDecimal factorAdherencia;
}

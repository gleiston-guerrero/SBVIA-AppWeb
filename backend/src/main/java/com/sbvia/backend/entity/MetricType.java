package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * <p>MetricType class.</p>
 *
 * @author Keitho_
 */
@Entity
@Table(name = "tipo_metrica")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class MetricType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_metrica")
    private Integer idTipoMetrica;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "unidad_medida", length = 50)
    private String unidadMedida;

    @Column(name = "descripcion", length = 255)
    private String description;

    @Column(name = "valor_minimo")
    private BigDecimal valorMinimo;

    @Column(name = "valor_maximo")
    private BigDecimal valorMaximo;
}

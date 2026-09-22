package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * JPA entity that defines a vehicle available in the simulator.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "vehicle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idVehiculo;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "brand", length = 100)
    private String marca;

    @Column(name = "model", length = 100)
    private String modelo;

    @Column(name = "model_year")
    private Integer anio;

    @Column(name = "transmission", nullable = false, length = 50)
    private String transmision;

    @Column(name = "max_speed_kmh")
    private BigDecimal velocidadMaximaKmh;

    @Column(name = "horsepower")
    private BigDecimal potenciaHp;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;
}

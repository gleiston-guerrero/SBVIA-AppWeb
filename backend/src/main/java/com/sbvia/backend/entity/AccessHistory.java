package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * JPA entity that records a user's sign-in history.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "access_history")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class AccessHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idHistorialAcceso;

    @Column(name = "occurred_at", nullable = false)
    @Builder.Default
    private Instant fechaHora = Instant.now();

    @Column(name = "ip_address")
    private String direccionIp;

    @Column(name = "device", length = 255)
    private String dispositivo;

    @Column(name = "browser", length = 255)
    private String navegador;

    @Column(name = "successful_login", nullable = false)
    private boolean accesoExitoso;

    @Column(name = "detail", length = 255)
    private String detalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

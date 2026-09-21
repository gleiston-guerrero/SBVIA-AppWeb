package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Vehicle} entities.
 *
 * @author Keitho_
 */
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    /**
     * <p>findFirstByActivoTrueOrderByIdVehiculoAsc.</p>
     *
     * @return a {@link java.util.Optional} object
     */
    Optional<Vehicle> findFirstByActivoTrueOrderByIdVehiculoAsc();
}

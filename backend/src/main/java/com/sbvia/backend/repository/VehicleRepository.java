package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findFirstByActivoTrueOrderByIdVehiculoAsc();
}

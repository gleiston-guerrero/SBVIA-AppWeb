package com.sbvia.backend.repository;

import com.sbvia.backend.entity.SimulationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SimulationStateRepository extends JpaRepository<SimulationState, Integer> {

    Optional<SimulationState> findByNombre(String name);
}

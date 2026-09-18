package com.sbvia.backend.repository;

import com.sbvia.backend.entity.SimulationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * <p>SimulationStateRepository interface.</p>
 *
 * @author Keitho_
 */
@Repository
public interface SimulationStateRepository extends JpaRepository<SimulationState, Integer> {

    /**
     * <p>findByName.</p>
     *
     * @param name a {@link java.lang.String} object
     * @return a {@link java.util.Optional} object
     */
    Optional<SimulationState> findByName(String name);
}

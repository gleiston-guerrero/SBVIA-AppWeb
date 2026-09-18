package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Infraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>InfractionRepository interface.</p>
 *
 * @author Keitho_
 */
@Repository
public interface InfractionRepository extends JpaRepository<Infraction, Integer> {

    /**
     * <p>findBySimulation_SimulationId.</p>
     *
     * @param simulationId a {@link java.lang.Integer} object
     * @return a {@link java.util.List} object
     */
    List<Infraction> findBySimulation_SimulationId(Integer simulationId);
}

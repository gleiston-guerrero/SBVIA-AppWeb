package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>FeedbackRepository interface.</p>
 *
 * @author Keitho_
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    /**
     * <p>findBySimulation_SimulationId.</p>
     *
     * @param simulationId a {@link java.lang.Integer} object
     * @return a {@link java.util.List} object
     */
    List<Feedback> findBySimulation_SimulationId(Integer simulationId);
}

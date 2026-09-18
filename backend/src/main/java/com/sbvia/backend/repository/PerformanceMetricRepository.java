package com.sbvia.backend.repository;

import com.sbvia.backend.entity.PerformanceMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>PerformanceMetricRepository interface.</p>
 *
 * @author Keitho_
 */
@Repository
public interface PerformanceMetricRepository extends JpaRepository<PerformanceMetric, Integer> {

    /**
     * <p>findBySimulation_SimulationId.</p>
     *
     * @param simulationId a {@link java.lang.Integer} object
     * @return a {@link java.util.List} object
     */
    List<PerformanceMetric> findBySimulation_SimulationId(Integer simulationId);
}

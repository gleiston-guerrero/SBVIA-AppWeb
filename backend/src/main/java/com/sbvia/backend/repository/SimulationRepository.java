package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>SimulationRepository interface.</p>
 *
 * @author Keitho_
 */
@Repository
public interface SimulationRepository extends JpaRepository<Simulation, Integer> {

    /**
     * <p>findByUser_UserIdOrderBySimulationIdDesc.</p>
     *
     * @param userId a {@link java.lang.Integer} object
     * @return a {@link java.util.List} object
     */
    List<Simulation> findByUser_UserIdOrderBySimulationIdDesc(Integer userId);

    /**
     * <p>findAllByOrderBySimulationIdDesc.</p>
     *
     * @return a {@link java.util.List} object
     */
    List<Simulation> findAllByOrderBySimulationIdDesc();

    /**
     * <p>getGlobalStats.</p>
     *
     * @return an array of {@link java.lang.Object} objects
     */
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(s), AVG(s.finalScore), SUM(CASE WHEN s.finalScore >= 70 THEN 1 ELSE 0 END) FROM Simulation s WHERE s.completed = true")
    Object[] getGlobalStats();
}

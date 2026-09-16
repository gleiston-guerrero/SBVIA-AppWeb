package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulationRepository extends JpaRepository<Simulation, Integer> {

    List<Simulation> findByUsuario_IdUsuarioOrderByIdSimulacionDesc(Integer userId);

    List<Simulation> findAllByOrderByIdSimulacionDesc();

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(s), AVG(s.finalScore), SUM(CASE WHEN s.finalScore >= 70 THEN 1 ELSE 0 END) FROM Simulation s WHERE s.completed = true")
    Object[] getGlobalStats();
}

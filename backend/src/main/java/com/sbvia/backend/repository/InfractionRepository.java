package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Infraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InfractionRepository extends JpaRepository<Infraction, Integer> {

    List<Infraction> findBySimulation_SimulationId(Integer simulationId);
}

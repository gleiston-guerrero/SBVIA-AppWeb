package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Scenario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Integer> {

    Page<Scenario> findByActivoTrue(Pageable pageable);
}

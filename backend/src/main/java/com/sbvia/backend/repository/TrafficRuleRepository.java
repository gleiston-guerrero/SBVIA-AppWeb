package com.sbvia.backend.repository;

import com.sbvia.backend.entity.TrafficRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrafficRuleRepository extends JpaRepository<TrafficRule, Integer> {

    List<TrafficRule> findByActivaTrue();

    Optional<TrafficRule> findByCodigo(String codigo);
}

package com.sbvia.backend.repository;

import com.sbvia.backend.entity.MetricType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetricTypeRepository extends JpaRepository<MetricType, Integer> {

    Optional<MetricType> findByName(String name);
}

package com.sbvia.backend.repository;

import com.sbvia.backend.entity.SeverityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeverityLevelRepository extends JpaRepository<SeverityLevel, Integer> {

    Optional<SeverityLevel> findByName(String name);
}

package com.sbvia.backend.repository;

import com.sbvia.backend.entity.DifficultyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DifficultyLevelRepository extends JpaRepository<DifficultyLevel, Integer> {

    Optional<DifficultyLevel> findByName(String name);
}

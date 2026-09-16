package com.sbvia.backend.repository;

import com.sbvia.backend.entity.RoadType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoadTypeRepository extends JpaRepository<RoadType, Integer> {

    Optional<RoadType> findByNombre(String name);
}

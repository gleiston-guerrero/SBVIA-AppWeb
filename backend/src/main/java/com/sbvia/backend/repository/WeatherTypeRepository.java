package com.sbvia.backend.repository;

import com.sbvia.backend.entity.WeatherType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeatherTypeRepository extends JpaRepository<WeatherType, Integer> {

    Optional<WeatherType> findByNombre(String name);
}

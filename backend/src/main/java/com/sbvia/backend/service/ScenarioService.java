package com.sbvia.backend.service;

import com.sbvia.backend.dto.ScenarioDTO;
import com.sbvia.backend.dto.CacheablePage;
import com.sbvia.backend.entity.Scenario;
import com.sbvia.backend.entity.DifficultyLevel;
import com.sbvia.backend.entity.WeatherType;
import com.sbvia.backend.entity.RoadType;
import com.sbvia.backend.exception.ResourceNotFoundException;
import com.sbvia.backend.repository.ScenarioRepository;
import com.sbvia.backend.repository.DifficultyLevelRepository;
import com.sbvia.backend.repository.WeatherTypeRepository;
import com.sbvia.backend.repository.RoadTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository escenarioRepository;
    private final RoadTypeRepository tipoViaRepository;
    private final DifficultyLevelRepository nivelDificultadRepository;
    private final WeatherTypeRepository tipoClimaRepository;

    @Cacheable(value = "scenarios", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    @Transactional(readOnly = true)
    public Page<ScenarioDTO> listarActivos(Pageable pageable) {
        Page<ScenarioDTO> page = escenarioRepository.findByActivoTrue(pageable)
                .map(this::mapToDTO);
        return new CacheablePage<>(page);
    }

    @Transactional(readOnly = true)
    public Page<ScenarioDTO> buscarFiltrado(String roadType, Integer difficultyLevel, String clima, Pageable pageable) {
        Page<ScenarioDTO> page = escenarioRepository.findByActivoTrue(pageable)
                .map(this::mapToDTO);
        return new CacheablePage<>(page);
    }

    @Transactional(readOnly = true)
    public ScenarioDTO buscarPorId(Integer id) {
        Scenario scenario = escenarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Scenario no encontrado con ID: " + id));
        return mapToDTO(scenario);
    }

    @CacheEvict(value = "scenarios", allEntries = true)
    @Transactional
    public ScenarioDTO crear(ScenarioDTO dto) {
        RoadType roadType = tipoViaRepository.findByNombre(dto.getRoadType())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de vía no encontrado: " + dto.getRoadType()));
        DifficultyLevel nivel = nivelDificultadRepository.findByNombre(dto.getDifficultyLevel())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nivel de dificultad no encontrado: " + dto.getDifficultyLevel()));
        WeatherType clima = tipoClimaRepository.findByNombre(dto.getWeatherType())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de clima no encontrado: " + dto.getWeatherType()));

        Scenario scenario = Scenario.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .lengthKm(dto.getLengthKm())
                .estimatedTimeMinutes(dto.getEstimatedTimeMinutes())
                .trafficDensity(dto.getTrafficDensity())
                .roadType(roadType)
                .difficultyLevel(nivel)
                .weatherType(clima)
                .activo(true)
                .build();
        return mapToDTO(escenarioRepository.save(scenario));
    }

    @CacheEvict(value = "scenarios", allEntries = true)
    @Transactional
    public ScenarioDTO actualizar(Integer id, ScenarioDTO dto) {
        Scenario scenario = escenarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Scenario no encontrado con ID: " + id));
        scenario.setName(dto.getName());
        scenario.setDescription(dto.getDescription());
        scenario.setTrafficDensity(dto.getTrafficDensity());
        scenario = escenarioRepository.save(scenario);
        return mapToDTO(scenario);
    }

    @CacheEvict(value = "scenarios", allEntries = true)
    @Transactional
    public void eliminar(Integer id) {
        Scenario scenario = escenarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Scenario no encontrado con ID: " + id));
        scenario.setActivo(false);
        escenarioRepository.save(scenario);
    }

    private ScenarioDTO mapToDTO(Scenario scenario) {
        return ScenarioDTO.builder()
                .id(scenario.getScenarioId())
                .name(scenario.getName())
                .description(scenario.getDescription())
                .lengthKm(scenario.getLengthKm())
                .estimatedTimeMinutes(scenario.getEstimatedTimeMinutes())
                .trafficDensity(scenario.getTrafficDensity())
                .roadType(scenario.getRoadType() != null ? scenario.getRoadType().getName() : null)
                .difficultyLevel(scenario.getDifficultyLevel() != null ? scenario.getDifficultyLevel().getName() : null)
                .weatherType(scenario.getWeatherType() != null ? scenario.getWeatherType().getName() : null)
                .activo(scenario.isActivo())
                .build();
    }
}

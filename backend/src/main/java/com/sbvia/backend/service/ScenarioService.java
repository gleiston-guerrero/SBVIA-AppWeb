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

/**
 * Business logic for creating, updating and listing driving scenarios.
 *
 * @author Keitho_
 */
@Service
@RequiredArgsConstructor
public class ScenarioService {
    private final ScenarioRepository escenarioRepository;
    private final RoadTypeRepository tipoViaRepository;
    private final DifficultyLevelRepository nivelDificultadRepository;
    private final WeatherTypeRepository tipoClimaRepository;

    /**
     * Returns a cached page of active scenarios.
     *
     * @param pageable the pagination information
     * @return the page of active scenarios converted to DTOs
     */
    @Cacheable(value = "scenarios", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    @Transactional(readOnly = true)
    public Page<ScenarioDTO> listarActivos(Pageable pageable) {
        Page<ScenarioDTO> page = escenarioRepository.findByActivoTrue(pageable)
                .map(this::mapToDTO);
        return new CacheablePage<>(page);
    }

    /**
     * Returns a page of active scenarios. The filter arguments are accepted for API
     * compatibility but are not applied by the current query, which returns all active scenarios.
     *
     * @param roadType the road type to filter by
     * @param difficultyLevel the difficulty level id to filter by
     * @param clima the weather type to filter by
     * @param pageable the pagination information
     * @return the page of active scenarios converted to DTOs
     */
    @Transactional(readOnly = true)
    public Page<ScenarioDTO> findFiltered(String roadType, Integer difficultyLevel, String clima, Pageable pageable) {
        Page<ScenarioDTO> page = escenarioRepository.findByActivoTrue(pageable)
                .map(this::mapToDTO);
        return new CacheablePage<>(page);
    }

    /**
     * Returns the scenario with the given id as a DTO.
     *
     * @param id the id of the scenario to find
     * @return the DTO with the data of the found scenario
     */
    @Transactional(readOnly = true)
    public ScenarioDTO findById(Integer id) {
        Scenario scenario = escenarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Scenario no encontrado con ID: " + id));
        return mapToDTO(scenario);
    }

    /**
     * Creates a new active scenario, resolving its road type, difficulty level, and
     * weather type by name, and evicts the scenarios cache.
     *
     * @param dto the scenario data to persist
     * @return the DTO of the created scenario
     */
    @CacheEvict(value = "scenarios", allEntries = true)
    @Transactional
    public ScenarioDTO create(ScenarioDTO dto) {
        RoadType roadType = tipoViaRepository.findByName(dto.getRoadType())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de vía no encontrado: " + dto.getRoadType()));
        DifficultyLevel nivel = nivelDificultadRepository.findByName(dto.getDifficultyLevel())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nivel de dificultad no encontrado: " + dto.getDifficultyLevel()));
        WeatherType clima = tipoClimaRepository.findByName(dto.getWeatherType())
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

    /**
     * Updates the name, description, and traffic density of the scenario with the given
     * id and evicts the scenarios cache.
     *
     * @param id the id of the scenario to update
     * @param dto the new values for the scenario
     * @return the DTO of the updated scenario
     */
    @CacheEvict(value = "scenarios", allEntries = true)
    @Transactional
    public ScenarioDTO update(Integer id, ScenarioDTO dto) {
        Scenario scenario = escenarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Scenario no encontrado con ID: " + id));
        scenario.setName(dto.getName());
        scenario.setDescription(dto.getDescription());
        scenario.setTrafficDensity(dto.getTrafficDensity());
        scenario = escenarioRepository.save(scenario);
        return mapToDTO(scenario);
    }

    /**
     * Soft-deletes the scenario with the given id by marking it inactive and evicts the
     * scenarios cache.
     *
     * @param id the id of the scenario to deactivate
     */
    @CacheEvict(value = "scenarios", allEntries = true)
    @Transactional
    public void delete(Integer id) {
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

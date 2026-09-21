package com.sbvia.backend.service;

import com.sbvia.backend.dto.ScenarioDTO;
import com.sbvia.backend.entity.Scenario;
import com.sbvia.backend.entity.RoadType;
import com.sbvia.backend.entity.DifficultyLevel;
import com.sbvia.backend.entity.WeatherType;
import com.sbvia.backend.repository.ScenarioRepository;
import com.sbvia.backend.repository.DifficultyLevelRepository;
import com.sbvia.backend.repository.WeatherTypeRepository;
import com.sbvia.backend.repository.RoadTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScenarioServiceTest {

    @Mock
    private ScenarioRepository escenarioRepository;

    @Mock
    private RoadTypeRepository tipoViaRepository;

    @Mock
    private DifficultyLevelRepository nivelDificultadRepository;

    @Mock
    private WeatherTypeRepository tipoClimaRepository;

    @InjectMocks
    private ScenarioService scenarioService;

    private RoadType buildRoadType(String name) {
        return RoadType.builder().idTipoVia(1).name(name).build();
    }

    private DifficultyLevel buildDifficultyLevel(Integer value) {
        return DifficultyLevel.builder().idNivelDificultad(1).name("Nivel " + value).value(value).build();
    }

    private WeatherType buildWeatherType(String name) {
        return WeatherType.builder().idTipoClima(1).name(name).build();
    }

    private Scenario buildScenario(Integer id, String name) {
        return Scenario.builder()
                .scenarioId(id)
                .name(name)
                .description("Descripción de " + name)
                .roadType(buildRoadType("Urbana"))
                .difficultyLevel(buildDifficultyLevel(2))
                .weatherType(buildWeatherType("Soleado"))
                .trafficDensity("Media")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("listarActivos: delega paginación al repositorio y mapea resultado correctamente")
    void listActive_maps_pagination() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Scenario> escenarios = List.of(
                buildScenario(1, "Autopista Norte"),
                buildScenario(2, "Centro Histórico")
        );
        Page<Scenario> pageResult = new PageImpl<>(escenarios, pageable, 2);

        when(escenarioRepository.findByActivoTrue(pageable)).thenReturn(pageResult);

        Page<ScenarioDTO> result = scenarioService.listActive(pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Autopista Norte");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Centro Histórico");
        verify(escenarioRepository, times(1)).findByActivoTrue(pageable);
    }

    @Test
    @DisplayName("findById: retorna DTO cuando el escenario existe")
    void findById_existing() {
        Scenario escenario = buildScenario(1, "Zona Industrial");
        when(escenarioRepository.findById(1)).thenReturn(Optional.of(escenario));

        ScenarioDTO dto = scenarioService.findById(1);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Zona Industrial");
        assertThat(dto.getRoadType()).isEqualTo("Urbana");
    }

    @Test
    @DisplayName("findById: lanza ResourceNotFoundException cuando no existe")
    void findById_missing_throws() {
        when(escenarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scenarioService.findById(99))
                .isInstanceOf(com.sbvia.backend.exception.ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create: persiste entidad y retorna DTO con ID asignado")
    void create_persists_scenario() {
        ScenarioDTO dto = ScenarioDTO.builder()
                .name("Redonda del Sur")
                .description("Intersección compleja")
                .roadType("Urbana")
                .difficultyLevel("Intermedio")
                .weatherType("Lluvia")
                .trafficDensity("Alta")
                .build();

        Scenario saved = buildScenario(5, "Redonda del Sur");
        when(tipoViaRepository.findByName("Urbana")).thenReturn(Optional.of(buildRoadType("Urbana")));
        when(nivelDificultadRepository.findByName("Intermedio"))
                .thenReturn(Optional.of(DifficultyLevel.builder().idNivelDificultad(2).name("Intermedio").value(2).build()));
        when(tipoClimaRepository.findByName("Lluvia")).thenReturn(Optional.of(buildWeatherType("Lluvia")));
        when(escenarioRepository.save(any(Scenario.class))).thenReturn(saved);

        ScenarioDTO result = scenarioService.create(dto);

        assertThat(result.getId()).isEqualTo(5);
        assertThat(result.getName()).isEqualTo("Redonda del Sur");
        verify(escenarioRepository, times(1)).save(any(Scenario.class));
    }

    @Test
    @DisplayName("delete: hace soft-delete (activo=false) sin borrar el registro")
    void delete_softDelete() {
        Scenario escenario = buildScenario(3, "Scenario A borrar");
        when(escenarioRepository.findById(3)).thenReturn(Optional.of(escenario));
        when(escenarioRepository.save(any(Scenario.class))).thenReturn(escenario);

        scenarioService.delete(3);

        assertThat(escenario.isActivo()).isFalse();
        verify(escenarioRepository, times(1)).save(escenario);
        verify(escenarioRepository, never()).deleteById(any());
    }
}

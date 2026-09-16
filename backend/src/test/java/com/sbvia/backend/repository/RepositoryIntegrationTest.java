package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Scenario;
import com.sbvia.backend.entity.UserState;
import com.sbvia.backend.entity.Role;
import com.sbvia.backend.entity.Simulation;
import com.sbvia.backend.entity.RoadType;
import com.sbvia.backend.entity.DifficultyLevel;
import com.sbvia.backend.entity.WeatherType;
import com.sbvia.backend.entity.User;
import com.sbvia.backend.entity.VehicleType;
import com.sbvia.backend.entity.Vehicle;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RepositoryIntegrationTest {

    @Autowired
    private ScenarioRepository escenarioRepository;

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private RoleRepository rolRepository;

    @Autowired
    private SimulationRepository simulacionRepository;

    @Autowired
    private EntityManager entityManager;

    private Role rolUsuario;
    private UserState estadoActivo;

    private RoadType persistTipoVia(String name) {
        RoadType tv = RoadType.builder().name(name).build();
        entityManager.persist(tv);
        return tv;
    }

    private DifficultyLevel persistNivelDificultad(Integer value) {
        DifficultyLevel nd = DifficultyLevel.builder().name("Nivel " + value).value(value).build();
        entityManager.persist(nd);
        return nd;
    }

    private WeatherType persistTipoClima(String name) {
        WeatherType tc = WeatherType.builder().name(name).build();
        entityManager.persist(tc);
        return tc;
    }

    @BeforeEach
    void setUp() {
        rolUsuario = rolRepository.save(
                Role.builder().name("ROLE_USER").description("User estándar").build()
        );
        estadoActivo = entityManager.merge(UserState.builder()
                .name("ACTIVO").description("Cuenta habilitada").permiteAcceso(true).build());
    }

    @Test
    @DisplayName("findByActivoTrue devuelve solo escenarios activos")
    void findByActivoTrue_devuelveSoloEscenariosActivos() {
        RoadType tv = persistTipoVia("AVENIDA");
        DifficultyLevel nd = persistNivelDificultad(1);
        WeatherType tc = persistTipoClima("Soleado");

        escenarioRepository.save(Scenario.builder()
                .name("Av. Amazonas")
                .roadType(tv)
                .difficultyLevel(nd)
                .weatherType(tc)
                .trafficDensity("Baja")
                .activo(true)
                .build());
        escenarioRepository.save(Scenario.builder()
                .name("Carretera Panamericana")
                .roadType(tv)
                .difficultyLevel(nd)
                .weatherType(tc)
                .trafficDensity("Media")
                .activo(true)
                .build());
        escenarioRepository.save(Scenario.builder()
                .name("Scenario desactivado")
                .roadType(tv)
                .difficultyLevel(nd)
                .weatherType(tc)
                .trafficDensity("Baja")
                .activo(false)
                .build());

        Page<Scenario> resultado = escenarioRepository.findByActivoTrue(PageRequest.of(0, 10));

        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getContent())
                .extracting(Scenario::getName)
                .containsExactlyInAnyOrder("Av. Amazonas", "Carretera Panamericana");
    }

    @Test
    @DisplayName("findByCorreo y existsByCorreo resuelven la autenticación")
    void findByCorreo_y_existsByCorreo_resuelvenAutenticacion() {
        User user = usuarioRepository.save(User.builder()
                .firstName("Jefferson")
                .lastName("Umaginga")
                .email("jumagingaa@uteq.edu.ec")
                .passwordHash("$2a$10$hashdemo")
                .accountLocked(false)
                .role(rolUsuario)
                .userState(estadoActivo)
                .build());

        Optional<User> encontrado = usuarioRepository.findByEmail("jumagingaa@uteq.edu.ec");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getUserId()).isEqualTo(user.getUserId());
        assertThat(usuarioRepository.existsByEmail("jumagingaa@uteq.edu.ec")).isTrue();
        assertThat(usuarioRepository.existsByEmail("noexiste@sbvia.test")).isFalse();
    }

    @Test
    @DisplayName("findByUsuario_IdUsuario lista las simulaciones de un user")
    void findByUsuario_IdUsuario_listaSimulacionesDeUsuario() {
        User user = usuarioRepository.save(User.builder()
                .firstName("Ana")
                .lastName("Perez")
                .email("ana.perez@sbvia.test")
                .passwordHash("$2a$10$hashdemo")
                .accountLocked(false)
                .role(rolUsuario)
                .userState(estadoActivo)
                .build());

        RoadType tv = persistTipoVia("CICLOVÍA");
        DifficultyLevel nd = persistNivelDificultad(1);
        WeatherType tc = persistTipoClima("Soleado");

        Scenario escenario = escenarioRepository.save(Scenario.builder()
                .name("Ciclovía Centro")
                .roadType(tv)
                .difficultyLevel(nd)
                .weatherType(tc)
                .trafficDensity("Baja")
                .activo(true)
                .build());
        VehicleType vehicleType = VehicleType.builder()
                .name("AUTOMOVIL").licenciaRequerida("B").build();
        entityManager.persist(vehicleType);
        Vehicle vehicle = Vehicle.builder()
                .name("Vehículo de prueba").transmision("MANUAL")
                .vehicleType(vehicleType).build();
        entityManager.persist(vehicle);

        simulacionRepository.save(Simulation.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .completed(true)
                .finalScore(new BigDecimal("8.5"))
                .user(user)
                .scenario(escenario)
                .vehicle(vehicle)
                .build());
        simulacionRepository.save(Simulation.builder()
                .startDate(LocalDate.now())
                .completed(false)
                .finalScore(new BigDecimal("0.0"))
                .user(user)
                .scenario(escenario)
                .vehicle(vehicle)
                .build());

        List<Simulation> simulaciones = simulacionRepository
                .findByUser_UserIdOrderBySimulationIdDesc(user.getUserId());

        assertThat(simulaciones).hasSize(2);
        assertThat(simulaciones)
                .extracting(Simulation::isCompleted)
                .containsExactlyInAnyOrder(true, false);
    }
}

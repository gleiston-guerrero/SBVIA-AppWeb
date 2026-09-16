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
                Role.builder().name("ROLE_USER").descripcion("User estándar").build()
        );
        estadoActivo = entityManager.merge(UserState.builder()
                .name("ACTIVO").descripcion("Cuenta habilitada").permiteAcceso(true).build());
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
                .densidadTrafico("Baja")
                .activo(true)
                .build());
        escenarioRepository.save(Scenario.builder()
                .name("Carretera Panamericana")
                .roadType(tv)
                .difficultyLevel(nd)
                .weatherType(tc)
                .densidadTrafico("Media")
                .activo(true)
                .build());
        escenarioRepository.save(Scenario.builder()
                .name("Scenario desactivado")
                .roadType(tv)
                .difficultyLevel(nd)
                .weatherType(tc)
                .densidadTrafico("Baja")
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
        User usuario = usuarioRepository.save(User.builder()
                .nombres("Jefferson")
                .apellidos("Umaginga")
                .correo("jumagingaa@uteq.edu.ec")
                .contrasenaHash("$2a$10$hashdemo")
                .accountLocked(false)
                .rol(rolUsuario)
                .estadoUsuario(estadoActivo)
                .build());

        Optional<User> encontrado = usuarioRepository.findByCorreo("jumagingaa@uteq.edu.ec");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getIdUsuario()).isEqualTo(usuario.getIdUsuario());
        assertThat(usuarioRepository.existsByCorreo("jumagingaa@uteq.edu.ec")).isTrue();
        assertThat(usuarioRepository.existsByCorreo("noexiste@sbvia.test")).isFalse();
    }

    @Test
    @DisplayName("findByUsuario_IdUsuario lista las simulaciones de un usuario")
    void findByUsuario_IdUsuario_listaSimulacionesDeUsuario() {
        User usuario = usuarioRepository.save(User.builder()
                .nombres("Ana")
                .apellidos("Perez")
                .correo("ana.perez@sbvia.test")
                .contrasenaHash("$2a$10$hashdemo")
                .accountLocked(false)
                .rol(rolUsuario)
                .estadoUsuario(estadoActivo)
                .build());

        RoadType tv = persistTipoVia("CICLOVÍA");
        DifficultyLevel nd = persistNivelDificultad(1);
        WeatherType tc = persistTipoClima("Soleado");

        Scenario escenario = escenarioRepository.save(Scenario.builder()
                .name("Ciclovía Centro")
                .roadType(tv)
                .difficultyLevel(nd)
                .weatherType(tc)
                .densidadTrafico("Baja")
                .activo(true)
                .build());
        VehicleType tipoVehiculo = VehicleType.builder()
                .name("AUTOMOVIL").licenciaRequerida("B").build();
        entityManager.persist(tipoVehiculo);
        Vehicle vehiculo = Vehicle.builder()
                .name("Vehículo de prueba").transmision("MANUAL")
                .tipoVehiculo(tipoVehiculo).build();
        entityManager.persist(vehiculo);

        simulacionRepository.save(Simulation.builder()
                .fechaInicio(LocalDate.now())
                .endDate(LocalDate.now())
                .completed(true)
                .finalScore(new BigDecimal("8.5"))
                .usuario(usuario)
                .escenario(escenario)
                .vehiculo(vehiculo)
                .build());
        simulacionRepository.save(Simulation.builder()
                .fechaInicio(LocalDate.now())
                .completed(false)
                .finalScore(new BigDecimal("0.0"))
                .usuario(usuario)
                .escenario(escenario)
                .vehiculo(vehiculo)
                .build());

        List<Simulation> simulaciones = simulacionRepository
                .findByUsuario_IdUsuarioOrderByIdSimulacionDesc(usuario.getIdUsuario());

        assertThat(simulaciones).hasSize(2);
        assertThat(simulaciones)
                .extracting(Simulation::isCompleted)
                .containsExactlyInAnyOrder(true, false);
    }
}

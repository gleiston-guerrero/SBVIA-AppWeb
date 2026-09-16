package com.sbvia.backend.service;

import com.sbvia.backend.dto.TrafficRuleDTO;
import com.sbvia.backend.entity.TrafficRule;
import com.sbvia.backend.exception.ResourceNotFoundException;
import com.sbvia.backend.repository.TrafficRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReglaTransitoServiceTest {
    @Mock private TrafficRuleRepository reglaRepository;
    private TrafficRuleService service;

    @BeforeEach
    void setUp() {
        service = new TrafficRuleService(reglaRepository);
    }

    @Test
    void listaReglas() {
        when(reglaRepository.findAll()).thenReturn(List.of(
                TrafficRule.builder().idReglaTransito(8).codigo("RT-001").name("Límite 30 km/h")
                        .descripcion("Reducir velocidad").categoria("Velocidad")
                        .penalizacionBase(new BigDecimal("5.00")).activa(true).build()));

        List<TrafficRuleDTO> resultado = service.listar();

        assertThat(resultado).singleElement().satisfies(regla -> {
            assertThat(regla.getId()).isEqualTo(8);
            assertThat(regla.getCodigo()).isEqualTo("RT-001");
            assertThat(regla.getName()).isEqualTo("Límite 30 km/h");
        });
    }

    @Test
    void creaRegla() {
        TrafficRuleDTO entrada = TrafficRuleDTO.builder().codigo("RT-002").name("  Pare  ")
                .descripcion(" Detención total ").categoria(" Señalización ")
                .penalizacionBase(new BigDecimal("10.00")).build();
        when(reglaRepository.save(any())).thenAnswer(invocacion -> {
            TrafficRule regla = invocacion.getArgument(0);
            regla.setIdReglaTransito(10);
            return regla;
        });

        TrafficRuleDTO creada = service.crear(entrada);

        assertThat(creada.getId()).isEqualTo(10);
        assertThat(creada.getName()).isEqualTo("Pare");
        assertThat(creada.getCategoria()).isEqualTo("Señalización");
    }

    @Test
    void actualizaUnaReglaExistente() {
        TrafficRule existente = TrafficRule.builder().idReglaTransito(4).codigo("RT-003")
                .name("Velocidad").categoria("Velocidad").penalizacionBase(BigDecimal.ZERO).build();
        TrafficRuleDTO entrada = TrafficRuleDTO.builder().codigo("RT-003").name("Ceda el paso")
                .descripcion(null).categoria("Prioridad").penalizacionBase(new BigDecimal("8.00")).build();
        when(reglaRepository.findById(4)).thenReturn(Optional.of(existente));
        when(reglaRepository.save(existente)).thenReturn(existente);

        assertThat(service.actualizar(4, entrada).getName()).isEqualTo("Ceda el paso");
        assertThat(existente.getDescripcion()).isNull();
    }

    @Test
    void rechazaReglaInexistente() {
        when(reglaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(99, TrafficRuleDTO.builder().codigo("X").name("X").categoria("X").penalizacionBase(BigDecimal.ZERO).build()))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(reglaRepository, never()).save(any());
    }

    @Test
    void eliminaUnaReglaExistente() {
        TrafficRule existente = TrafficRule.builder().idReglaTransito(4).build();
        when(reglaRepository.findById(4)).thenReturn(Optional.of(existente));

        service.eliminar(4);

        verify(reglaRepository).delete(existente);
    }
}

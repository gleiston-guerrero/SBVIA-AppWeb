package com.sbvia.backend.service;

import com.sbvia.backend.dto.TrafficRuleDTO;
import com.sbvia.backend.entity.TrafficRule;
import com.sbvia.backend.exception.ResourceNotFoundException;
import com.sbvia.backend.repository.TrafficRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>TrafficRuleService class.</p>
 *
 * @author Keitho_
 */
@Service
@RequiredArgsConstructor
public class TrafficRuleService {
    private final TrafficRuleRepository trafficRuleRepository;

    /**
     * Returns all traffic rules converted to DTOs.
     *
     * @return the list of all traffic rules
     */
    @Transactional(readOnly = true)
    public List<TrafficRuleDTO> list() {
        return trafficRuleRepository.findAll().stream().map(this::toDTO).toList();
    }

    /**
     * Creates a new active traffic rule from the values of the given DTO.
     *
     * @param dto the traffic rule data to persist
     * @return the DTO of the created traffic rule
     */
    @Transactional
    public TrafficRuleDTO create(TrafficRuleDTO dto) {
        TrafficRule regla = TrafficRule.builder()
                .codigo(dto.getCodigo().trim())
                .nombre(dto.getNombre().trim())
                .descripcion(dto.getDescripcion())
                .categoria(dto.getCategoria().trim())
                .penalizacionBase(dto.getPenalizacionBase())
                .activa(true)
                .build();
        return toDTO(trafficRuleRepository.save(regla));
    }

    /**
     * Updates the traffic rule with the given id with the values of the given DTO.
     *
     * @param id the id of the traffic rule to update
     * @param dto the new traffic rule values
     * @return the DTO of the updated traffic rule
     */
    @Transactional
    public TrafficRuleDTO update(Integer id, TrafficRuleDTO dto) {
        TrafficRule regla = trafficRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regla de tránsito no encontrada con ID: " + id));
        regla.setCodigo(dto.getCodigo().trim());
        regla.setNombre(dto.getNombre().trim());
        regla.setDescripcion(dto.getDescripcion());
        regla.setCategoria(dto.getCategoria().trim());
        regla.setPenalizacionBase(dto.getPenalizacionBase());
        return toDTO(trafficRuleRepository.save(regla));
    }

    /**
     * Deletes the traffic rule with the given id.
     *
     * @param id the id of the traffic rule to delete
     */
    @Transactional
    public void delete(Integer id) {
        TrafficRule regla = trafficRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regla de tránsito no encontrada con ID: " + id));
        trafficRuleRepository.delete(regla);
    }

    private TrafficRuleDTO toDTO(TrafficRule regla) {
        return TrafficRuleDTO.builder()
                .id(regla.getIdReglaTransito())
                .codigo(regla.getCodigo())
                .nombre(regla.getNombre())
                .descripcion(regla.getDescripcion())
                .categoria(regla.getCategoria())
                .penalizacionBase(regla.getPenalizacionBase())
                .activa(regla.isActiva())
                .build();
    }
}

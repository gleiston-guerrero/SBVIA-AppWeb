package com.sbvia.backend.service;

import com.sbvia.backend.dto.TrafficRuleDTO;
import com.sbvia.backend.entity.TrafficRule;
import com.sbvia.backend.exception.ResourceNotFoundException;
import com.sbvia.backend.repository.TrafficRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrafficRuleService {
    private final TrafficRuleRepository trafficRuleRepository;

    @Transactional(readOnly = true)
    /**
     * Método público.
     */
    public List<TrafficRuleDTO> list() {
        return trafficRuleRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    /**
     * Método público.
     */
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

    @Transactional
    /**
     * Método público.
     */
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

    @Transactional
    /**
     * Método público.
     */
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

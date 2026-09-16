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
    private final TrafficRuleRepository reglaRepository;

    @Transactional(readOnly = true)
    public List<TrafficRuleDTO> listar() {
        return reglaRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public TrafficRuleDTO crear(TrafficRuleDTO dto) {
        TrafficRule regla = TrafficRule.builder()
                .codigo(dto.getCodigo().trim())
                .name(dto.getName().trim())
                .description(dto.getDescription())
                .categoria(dto.getCategoria().trim())
                .penalizacionBase(dto.getPenalizacionBase())
                .activa(true)
                .build();
        return toDTO(reglaRepository.save(regla));
    }

    @Transactional
    public TrafficRuleDTO actualizar(Integer id, TrafficRuleDTO dto) {
        TrafficRule regla = reglaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regla de tránsito no encontrada con ID: " + id));
        regla.setCodigo(dto.getCodigo().trim());
        regla.setName(dto.getName().trim());
        regla.setDescription(dto.getDescription());
        regla.setCategoria(dto.getCategoria().trim());
        regla.setPenalizacionBase(dto.getPenalizacionBase());
        return toDTO(reglaRepository.save(regla));
    }

    @Transactional
    public void eliminar(Integer id) {
        TrafficRule regla = reglaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regla de tránsito no encontrada con ID: " + id));
        reglaRepository.delete(regla);
    }

    private TrafficRuleDTO toDTO(TrafficRule regla) {
        return TrafficRuleDTO.builder()
                .id(regla.getIdReglaTransito())
                .codigo(regla.getCodigo())
                .name(regla.getName())
                .description(regla.getDescription())
                .categoria(regla.getCategoria())
                .penalizacionBase(regla.getPenalizacionBase())
                .activa(regla.isActiva())
                .build();
    }
}

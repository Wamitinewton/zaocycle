package com.newton.zaocycle.chemical.application;

import com.newton.zaocycle.chemical.application.dto.ChemicalSummary;
import com.newton.zaocycle.chemical.domain.model.Chemical;
import com.newton.zaocycle.chemical.domain.port.ChemicalRepository;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
class ChemicalServiceImpl implements ChemicalService {

    private final ChemicalRepository repository;

    ChemicalServiceImpl(ChemicalRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Chemical> listAllActive() {
        return repository.findAllActive();
    }

    @Override
    public List<ChemicalSummary> listForCrop(String crop) {
        return repository.findActiveByCrop(crop).stream()
                .map(c -> new ChemicalSummary(c.id(), c.name(), c.halfLifeDays(), c.phiDays()))
                .toList();
    }

    @Override
    public Chemical getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Chemical not found: " + id));
    }
}

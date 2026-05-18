package com.newton.zaocycle.chemical.infrastructure.persistence;

import com.newton.zaocycle.chemical.domain.model.Chemical;
import com.newton.zaocycle.chemical.domain.port.ChemicalRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class ChemicalRepositoryAdapter implements ChemicalRepository {

    private final ChemicalJpaRepository jpa;

    ChemicalRepositoryAdapter(ChemicalJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Chemical> findAllActive() {
        return jpa.findByActiveTrue().stream()
                .map(ChemicalEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Chemical> findActiveByCrop(String crop) {
        return jpa.findActiveByCrop(crop).stream()
                .map(ChemicalEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Chemical> findById(UUID id) {
        return jpa.findById(id).map(ChemicalEntityMapper::toDomain);
    }
}

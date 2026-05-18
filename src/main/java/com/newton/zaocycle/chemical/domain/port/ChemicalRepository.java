package com.newton.zaocycle.chemical.domain.port;

import com.newton.zaocycle.chemical.domain.model.Chemical;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChemicalRepository {
    List<Chemical> findAllActive();
    List<Chemical> findActiveByCrop(String crop);
    Optional<Chemical> findById(UUID id);
}

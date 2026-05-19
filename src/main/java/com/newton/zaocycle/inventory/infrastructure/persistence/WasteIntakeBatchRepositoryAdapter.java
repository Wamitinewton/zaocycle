package com.newton.zaocycle.inventory.infrastructure.persistence;

import com.newton.zaocycle.inventory.domain.model.WasteIntakeBatch;
import com.newton.zaocycle.inventory.domain.port.WasteIntakeBatchRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class WasteIntakeBatchRepositoryAdapter implements WasteIntakeBatchRepository {

    private final WasteIntakeBatchJpaRepository jpa;

    WasteIntakeBatchRepositoryAdapter(WasteIntakeBatchJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public WasteIntakeBatch save(WasteIntakeBatch batch) {
        WasteIntakeBatchEntity entity = WasteIntakeBatchEntityMapper.toEntity(batch);
        return WasteIntakeBatchEntityMapper.toDomain(jpa.save(entity));
    }

    @Override
    public List<WasteIntakeBatch> findAll() {
        return jpa.findAll().stream()
                .map(WasteIntakeBatchEntityMapper::toDomain)
                .toList();
    }
}

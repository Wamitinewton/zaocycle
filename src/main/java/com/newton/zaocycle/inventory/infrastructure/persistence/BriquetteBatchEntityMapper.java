package com.newton.zaocycle.inventory.infrastructure.persistence;

import com.newton.zaocycle.inventory.domain.model.BriquetteBatch;

final class BriquetteBatchEntityMapper {

    private BriquetteBatchEntityMapper() {
    }

    static BriquetteBatch toDomain(BriquetteBatchEntity e) {
        return new BriquetteBatch(
                e.getId(), e.getBatchNumber(), e.getKgProduced(),
                e.getKgRemaining(), e.getProducedAt(),
                e.getSourceIntakeId(), e.getCreatedAt()
        );
    }

    static BriquetteBatchEntity toEntity(BriquetteBatch domain) {
        BriquetteBatchEntity e = new BriquetteBatchEntity();
        e.setId(domain.id());
        e.setBatchNumber(domain.batchNumber());
        e.setKgProduced(domain.kgProduced());
        e.setKgRemaining(domain.kgRemaining());
        e.setProducedAt(domain.producedAt());
        e.setSourceIntakeId(domain.sourceIntakeId());
        return e;
    }
}

package com.newton.zaocycle.inventory.infrastructure.persistence;

import com.newton.zaocycle.inventory.domain.model.BriquetteBatch;
import com.newton.zaocycle.inventory.domain.port.BriquetteBatchRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
class BriquetteBatchRepositoryAdapter implements BriquetteBatchRepository {

    private final BriquetteBatchJpaRepository jpa;

    BriquetteBatchRepositoryAdapter(BriquetteBatchJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public BriquetteBatch save(BriquetteBatch batch) {
        BriquetteBatchEntity entity = BriquetteBatchEntityMapper.toEntity(batch);
        return BriquetteBatchEntityMapper.toDomain(jpa.save(entity));
    }

    @Override
    public List<BriquetteBatch> findWithRemainingStockOrderedByOldest() {
        return jpa.findWithRemainingStockOrderedByOldest().stream()
                .map(BriquetteBatchEntityMapper::toDomain)
                .toList();
    }

    @Override
    public BigDecimal sumRemaining() {
        return jpa.sumRemaining();
    }
}

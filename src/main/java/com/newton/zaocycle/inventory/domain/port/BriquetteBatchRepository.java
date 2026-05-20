package com.newton.zaocycle.inventory.domain.port;

import com.newton.zaocycle.inventory.domain.model.BriquetteBatch;

import java.math.BigDecimal;
import java.util.List;

public interface BriquetteBatchRepository {
    BriquetteBatch save(BriquetteBatch batch);

    List<BriquetteBatch> findWithRemainingStockOrderedByOldest();

    BigDecimal sumRemaining();
}

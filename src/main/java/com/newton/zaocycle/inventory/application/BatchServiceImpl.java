package com.newton.zaocycle.inventory.application;

import com.newton.zaocycle.inventory.application.command.CreateBatchCommand;
import com.newton.zaocycle.inventory.domain.model.BriquetteBatch;
import com.newton.zaocycle.inventory.domain.port.BriquetteBatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
class BatchServiceImpl implements BatchService {

    private final BriquetteBatchRepository batchRepository;

    BatchServiceImpl(BriquetteBatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @Override
    @Transactional
    public BriquetteBatch create(CreateBatchCommand cmd) {
        BriquetteBatch batch = BriquetteBatch.create(
                cmd.batchNumber(), cmd.kgProduced(), cmd.producedAt(), cmd.sourceIntakeId());
        return batchRepository.save(batch);
    }

    @Override
    public List<BriquetteBatch> findAll() {
        return batchRepository.findWithRemainingStockOrderedByOldest();
    }

    @Override
    public BigDecimal availableStock() {
        return batchRepository.sumRemaining();
    }
}

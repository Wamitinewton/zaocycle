package com.newton.zaocycle.inventory.application;

import com.newton.zaocycle.inventory.application.command.CreateBatchCommand;
import com.newton.zaocycle.inventory.domain.model.BriquetteBatch;

import java.math.BigDecimal;
import java.util.List;

public interface BatchService {
    BriquetteBatch create(CreateBatchCommand cmd);
    List<BriquetteBatch> findAll();
    BigDecimal availableStock();
}

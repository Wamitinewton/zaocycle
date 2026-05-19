package com.newton.zaocycle.inventory.domain.port;

import com.newton.zaocycle.inventory.domain.model.WasteIntakeBatch;

import java.util.List;

public interface WasteIntakeBatchRepository {
    WasteIntakeBatch save(WasteIntakeBatch batch);
    List<WasteIntakeBatch> findAll();
}

package com.newton.zaocycle.inventory.application;

import com.newton.zaocycle.inventory.application.command.RecordIntakeCommand;
import com.newton.zaocycle.inventory.domain.model.WasteIntakeBatch;

import java.util.List;

public interface IntakeService {
    WasteIntakeBatch record(RecordIntakeCommand cmd);
    List<WasteIntakeBatch> findAll();
}

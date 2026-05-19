package com.newton.zaocycle.inventory.application;

import com.newton.zaocycle.inventory.application.command.RecordIntakeCommand;
import com.newton.zaocycle.inventory.domain.model.WasteIntakeBatch;
import com.newton.zaocycle.inventory.domain.port.WasteIntakeBatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
class IntakeServiceImpl implements IntakeService {

    private final WasteIntakeBatchRepository intakeRepository;

    IntakeServiceImpl(WasteIntakeBatchRepository intakeRepository) {
        this.intakeRepository = intakeRepository;
    }

    @Override
    @Transactional
    public WasteIntakeBatch record(RecordIntakeCommand cmd) {
        WasteIntakeBatch batch = WasteIntakeBatch.create(
                cmd.intakeDate(), cmd.totalKg(), cmd.pickupIds(),
                cmd.notes(), cmd.recordedBy());
        return intakeRepository.save(batch);
    }

    @Override
    public List<WasteIntakeBatch> findAll() {
        return intakeRepository.findAll();
    }
}

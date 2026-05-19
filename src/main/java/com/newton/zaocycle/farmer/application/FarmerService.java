package com.newton.zaocycle.farmer.application;

import com.newton.zaocycle.farmer.application.command.RegisterFarmerCommand;
import com.newton.zaocycle.farmer.application.dto.FarmerSummary;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.shared.domain.PhoneNumber;

import java.util.Optional;
import java.util.UUID;

public interface FarmerService {
    Optional<Farmer> findByPhone(PhoneNumber phone);
    Optional<Farmer> findById(UUID id);
    Farmer findOrCreateByPhone(PhoneNumber phone);
    Farmer register(RegisterFarmerCommand command);
    FarmerSummary getSummary(PhoneNumber phone);
}

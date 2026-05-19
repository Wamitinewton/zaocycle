package com.newton.zaocycle.rider.application;

import com.newton.zaocycle.rider.application.command.RegisterRiderCommand;
import com.newton.zaocycle.rider.domain.model.Rider;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.domain.Ward;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RiderService {
    Rider register(RegisterRiderCommand cmd);
    Optional<Rider> findById(UUID id);
    Optional<Rider> findByPhone(PhoneNumber phone);
    List<Rider> findActiveInWard(Ward ward);
    Rider deactivate(UUID id);
}

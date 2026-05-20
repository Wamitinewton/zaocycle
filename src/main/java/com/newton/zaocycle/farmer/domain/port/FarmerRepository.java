package com.newton.zaocycle.farmer.domain.port;

import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.shared.domain.PhoneNumber;

import java.util.Optional;
import java.util.UUID;

public interface FarmerRepository {
    Optional<Farmer> findByPhone(PhoneNumber phone);

    Optional<Farmer> findById(UUID id);

    Farmer save(Farmer farmer);
}

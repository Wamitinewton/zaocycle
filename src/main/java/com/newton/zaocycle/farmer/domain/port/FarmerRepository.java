package com.newton.zaocycle.farmer.domain.port;

import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.shared.domain.PhoneNumber;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FarmerRepository {
    Optional<Farmer> findByPhone(PhoneNumber phone);

    Optional<Farmer> findById(UUID id);

    List<Farmer> findAllByIds(Collection<UUID> ids);

    Farmer save(Farmer farmer);
}

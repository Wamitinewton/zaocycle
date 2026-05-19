package com.newton.zaocycle.rider.domain.port;

import com.newton.zaocycle.rider.domain.model.Rider;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.domain.Ward;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RiderRepository {
    Optional<Rider> findById(UUID id);
    Optional<Rider> findByPhone(PhoneNumber phone);
    List<Rider> findActiveByWard(Ward ward);
    Rider save(Rider rider);
}

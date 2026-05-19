package com.newton.zaocycle.rider.infrastructure.persistence;

import com.newton.zaocycle.rider.domain.model.Rider;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.domain.Ward;

final class RiderEntityMapper {

    private RiderEntityMapper() {}

    static Rider toDomain(RiderEntity e) {
        return new Rider(
                e.getId(),
                PhoneNumber.of(e.getPhone()),
                e.getFullName(),
                e.getPasswordHash(),
                Ward.valueOf(e.getAssignedWard()),
                e.isActive(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    static RiderEntity toEntity(Rider rider) {
        RiderEntity e = new RiderEntity();
        e.setId(rider.id());
        e.setPhone(rider.phone().value());
        e.setFullName(rider.fullName());
        e.setPasswordHash(rider.passwordHash());
        e.setAssignedWard(rider.assignedWard().name());
        e.setActive(rider.isActive());
        return e;
    }
}

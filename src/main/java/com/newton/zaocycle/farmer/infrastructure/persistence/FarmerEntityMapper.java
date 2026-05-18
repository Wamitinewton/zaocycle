package com.newton.zaocycle.farmer.infrastructure.persistence;

import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.domain.Ward;

final class FarmerEntityMapper {

    private FarmerEntityMapper() {}

    static Farmer toDomain(FarmerEntity e) {
        Ward ward = (e.getWard() != null) ? Ward.valueOf(e.getWard()) : null;
        return new Farmer(
                e.getId(),
                PhoneNumber.of(e.getPhone()),
                e.getFullName(),
                ward,
                e.getPinHash(),
                e.isRegistrationComplete(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    static FarmerEntity toEntity(Farmer farmer) {
        FarmerEntity e = new FarmerEntity();
        e.setId(farmer.id());
        e.setPhone(farmer.phone().value());
        e.setFullName(farmer.fullName());
        e.setWard(farmer.ward() != null ? farmer.ward().name() : null);
        e.setPinHash(farmer.pinHash());
        e.setRegistrationComplete(farmer.isRegistrationComplete());
        return e;
    }
}

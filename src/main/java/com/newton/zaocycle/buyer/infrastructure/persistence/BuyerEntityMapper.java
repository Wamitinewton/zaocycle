package com.newton.zaocycle.buyer.infrastructure.persistence;

import com.newton.zaocycle.buyer.domain.model.Buyer;
import com.newton.zaocycle.buyer.domain.model.BuyerType;

final class BuyerEntityMapper {

    private BuyerEntityMapper() {}

    static Buyer toDomain(BuyerEntity e) {
        return new Buyer(
                e.getId(),
                e.getEmail(),
                e.getPasswordHash(),
                e.getPhone(),
                BuyerType.valueOf(e.getBuyerType()),
                e.getDisplayName(),
                e.getContactPerson(),
                e.getAddress(),
                e.getWard(),
                e.isActive(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    static BuyerEntity toEntity(Buyer buyer) {
        BuyerEntity e = new BuyerEntity();
        e.setId(buyer.id());
        e.setEmail(buyer.email());
        e.setPasswordHash(buyer.passwordHash());
        e.setPhone(buyer.phone());
        e.setBuyerType(buyer.buyerType().name());
        e.setDisplayName(buyer.displayName());
        e.setContactPerson(buyer.contactPerson());
        e.setAddress(buyer.address());
        e.setWard(buyer.ward());
        e.setActive(buyer.isActive());
        return e;
    }
}

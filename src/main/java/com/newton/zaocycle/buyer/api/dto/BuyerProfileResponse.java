package com.newton.zaocycle.buyer.api.dto;

import com.newton.zaocycle.buyer.domain.model.Buyer;

public record BuyerProfileResponse(
        String id,
        String email,
        String phone,
        String buyerType,
        String displayName,
        String contactPerson,
        String address,
        String ward
) {
    public static BuyerProfileResponse from(Buyer buyer) {
        return new BuyerProfileResponse(
                buyer.id().toString(),
                buyer.email(),
                buyer.phone(),
                buyer.buyerType().name(),
                buyer.displayName(),
                buyer.contactPerson(),
                buyer.address(),
                buyer.ward()
        );
    }
}

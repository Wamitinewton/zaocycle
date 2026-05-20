package com.newton.zaocycle.farmer.api.dto;

import com.newton.zaocycle.farmer.domain.model.Farmer;

import java.util.UUID;

public record FarmerResponse(
        UUID id,
        String phone,
        String fullName,
        String ward,
        boolean registrationComplete,
        String tradingCenter,
        Double latitude,
        Double longitude
) {
    public static FarmerResponse from(Farmer f) {
        return new FarmerResponse(
                f.id(),
                f.phone().value(),
                f.fullName(),
                f.ward() != null ? f.ward().displayName() : null,
                f.isRegistrationComplete(),
                f.tradingCenter(),
                f.latitude(),
                f.longitude()
        );
    }
}

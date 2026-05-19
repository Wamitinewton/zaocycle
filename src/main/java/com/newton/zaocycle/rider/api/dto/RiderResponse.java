package com.newton.zaocycle.rider.api.dto;

import com.newton.zaocycle.rider.domain.model.Rider;

public record RiderResponse(String id, String phone, String fullName, String ward, boolean active) {

    public static RiderResponse from(Rider rider) {
        return new RiderResponse(
                rider.id().toString(),
                rider.phone().value(),
                rider.fullName(),
                rider.assignedWard().name(),
                rider.isActive()
        );
    }
}

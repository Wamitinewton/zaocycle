package com.newton.zaocycle.farmer.api.dto;

import com.newton.zaocycle.farmer.application.command.UpdateFarmerLocationCommand;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record UpdateFarmerLocationRequest(
        @NotBlank @Size(max = 100) String tradingCenter,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
) {
    public UpdateFarmerLocationCommand toCommand(UUID farmerId) {
        return new UpdateFarmerLocationCommand(farmerId, tradingCenter, latitude, longitude);
    }
}

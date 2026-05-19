package com.newton.zaocycle.buyer.api.dto;

import com.newton.zaocycle.buyer.application.command.UpdateBuyerCommand;
import jakarta.validation.constraints.NotBlank;

public record UpdateBuyerRequest(
        @NotBlank String displayName,
        String contactPerson,
        String address,
        String ward
) {
    public UpdateBuyerCommand toCommand() {
        return new UpdateBuyerCommand(displayName, contactPerson, address, ward);
    }
}

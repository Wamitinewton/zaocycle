package com.newton.zaocycle.buyer.api.dto;

import com.newton.zaocycle.buyer.application.command.RegisterBuyerCommand;
import com.newton.zaocycle.buyer.domain.model.BuyerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterBuyerRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String password,
        @NotBlank String phone,
        @NotNull BuyerType buyerType,
        @NotBlank String displayName,
        String contactPerson,
        String address,
        String ward
) {
    public RegisterBuyerCommand toCommand() {
        return new RegisterBuyerCommand(
                email, password, phone, buyerType, displayName, contactPerson, address, ward);
    }
}

package com.newton.zaocycle.buyer.application.command;

import com.newton.zaocycle.buyer.domain.model.BuyerType;

public record RegisterBuyerCommand(
        String email,
        String password,
        String phone,
        BuyerType buyerType,
        String displayName,
        String contactPerson,
        String address,
        String ward
) {
}

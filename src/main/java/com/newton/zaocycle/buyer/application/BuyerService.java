package com.newton.zaocycle.buyer.application;

import com.newton.zaocycle.buyer.application.command.RegisterBuyerCommand;
import com.newton.zaocycle.buyer.application.command.UpdateBuyerCommand;
import com.newton.zaocycle.buyer.domain.model.Buyer;

import java.util.Optional;
import java.util.UUID;

public interface BuyerService {
    Buyer register(RegisterBuyerCommand cmd);

    Optional<Buyer> findById(UUID id);

    Optional<Buyer> findByEmail(String email);

    Buyer updateProfile(UUID id, UpdateBuyerCommand cmd);

    Buyer updateProfileImage(UUID id, String imageUrl);
}

package com.newton.zaocycle.buyer.domain.port;

import com.newton.zaocycle.buyer.domain.model.Buyer;

import java.util.Optional;
import java.util.UUID;

public interface BuyerRepository {
    Optional<Buyer> findById(UUID id);
    Optional<Buyer> findByEmail(String email);
    Buyer save(Buyer buyer);
    boolean existsByEmail(String email);
}

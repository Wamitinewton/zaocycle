package com.newton.zaocycle.buyer.infrastructure.persistence;

import com.newton.zaocycle.buyer.domain.model.Buyer;
import com.newton.zaocycle.buyer.domain.port.BuyerRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class BuyerRepositoryAdapter implements BuyerRepository {

    private final BuyerJpaRepository jpa;

    BuyerRepositoryAdapter(BuyerJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Buyer> findById(UUID id) {
        return jpa.findById(id).map(BuyerEntityMapper::toDomain);
    }

    @Override
    public Optional<Buyer> findByEmail(String email) {
        return jpa.findByEmail(email).map(BuyerEntityMapper::toDomain);
    }

    @Override
    public Buyer save(Buyer buyer) {
        return BuyerEntityMapper.toDomain(jpa.save(BuyerEntityMapper.toEntity(buyer)));
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }
}

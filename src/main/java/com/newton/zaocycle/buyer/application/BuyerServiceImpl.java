package com.newton.zaocycle.buyer.application;

import com.newton.zaocycle.buyer.application.command.RegisterBuyerCommand;
import com.newton.zaocycle.buyer.application.command.UpdateBuyerCommand;
import com.newton.zaocycle.buyer.domain.model.Buyer;
import com.newton.zaocycle.buyer.domain.port.BuyerRepository;
import com.newton.zaocycle.shared.exception.NotFoundException;
import com.newton.zaocycle.shared.exception.ValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
class BuyerServiceImpl implements BuyerService {

    private final BuyerRepository repository;
    private final PasswordEncoder passwordEncoder;

    BuyerServiceImpl(BuyerRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Buyer register(RegisterBuyerCommand cmd) {
        if (repository.existsByEmail(cmd.email())) {
            throw new ValidationException("Email already registered");
        }
        String hash = passwordEncoder.encode(cmd.password());
        return repository.save(Buyer.create(
                cmd.email(), hash, cmd.phone(), cmd.buyerType(),
                cmd.displayName(), cmd.contactPerson(), cmd.address(), cmd.ward()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Buyer> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Buyer> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    @Transactional
    public Buyer updateProfile(UUID id, UpdateBuyerCommand cmd) {
        Buyer buyer = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Buyer not found: " + id));
        buyer.updateProfile(cmd.displayName(), cmd.contactPerson(), cmd.address(), cmd.ward());
        return repository.save(buyer);
    }

    @Override
    @Transactional
    public Buyer updateProfileImage(UUID id, String imageUrl) {
        Buyer buyer = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Buyer not found: " + id));
        buyer.updateProfileImage(imageUrl);
        return repository.save(buyer);
    }
}

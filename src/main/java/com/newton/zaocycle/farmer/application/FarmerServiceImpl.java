package com.newton.zaocycle.farmer.application;

import com.newton.zaocycle.farmer.application.command.RegisterFarmerCommand;
import com.newton.zaocycle.farmer.application.dto.FarmerSummary;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.farmer.domain.port.FarmerRepository;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

@Service
@Transactional
class FarmerServiceImpl implements FarmerService {

    private final FarmerRepository repository;

    FarmerServiceImpl(FarmerRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Farmer> findByPhone(PhoneNumber phone) {
        return repository.findByPhone(phone);
    }

    @Override
    public Farmer findOrCreateByPhone(PhoneNumber phone) {
        return repository.findByPhone(phone)
                .orElseGet(() -> repository.save(Farmer.newUnregistered(phone)));
    }

    @Override
    public Farmer register(RegisterFarmerCommand command) {
        Farmer farmer = repository.findByPhone(command.phone())
                .orElseThrow(() -> new NotFoundException("Farmer not found for: " + command.phone().value()));
        farmer.completeRegistration(command.fullName(), command.ward(), hashPin(command.pin()));
        return repository.save(farmer);
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerSummary getSummary(PhoneNumber phone) {
        Farmer farmer = repository.findByPhone(phone)
                .orElseThrow(() -> new NotFoundException("Farmer not found: " + phone.value()));
        return new FarmerSummary(farmer.id(), farmer.phone().value(),
                farmer.fullName(), farmer.isRegistrationComplete());
    }

    private String hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed by the JVM spec — this cannot happen
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}

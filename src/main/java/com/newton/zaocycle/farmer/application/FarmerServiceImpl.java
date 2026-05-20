package com.newton.zaocycle.farmer.application;

import com.newton.zaocycle.farmer.application.command.RegisterFarmerCommand;
import com.newton.zaocycle.farmer.application.command.UpdateFarmerLocationCommand;
import com.newton.zaocycle.farmer.application.dto.FarmerSummary;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.farmer.domain.port.FarmerRepository;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
class FarmerServiceImpl implements FarmerService {

    private final FarmerRepository repository;
    private final PasswordEncoder passwordEncoder;

    FarmerServiceImpl(FarmerRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Farmer> findByPhone(PhoneNumber phone) {
        return repository.findByPhone(phone);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Farmer> findById(UUID id) {
        return repository.findById(id);
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
        farmer.completeRegistration(
                command.fullName(),
                command.ward(),
                passwordEncoder.encode(command.pin()),
                command.tradingCenter(),
                command.latitude(),
                command.longitude()
        );
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

    @Override
    public Farmer updateProfileImage(UUID id, String imageUrl) {
        Farmer farmer = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Farmer not found: " + id));
        farmer.updateProfileImage(imageUrl);
        return repository.save(farmer);
    }

    @Override
    public Farmer updateLocation(UpdateFarmerLocationCommand command) {
        Farmer farmer = repository.findById(command.farmerId())
                .orElseThrow(() -> new NotFoundException("Farmer not found: " + command.farmerId()));
        farmer.updateLocation(command.tradingCenter(), command.latitude(), command.longitude());
        return repository.save(farmer);
    }
}

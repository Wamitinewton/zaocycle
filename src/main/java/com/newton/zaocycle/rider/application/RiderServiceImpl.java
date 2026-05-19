package com.newton.zaocycle.rider.application;

import com.newton.zaocycle.rider.application.command.RegisterRiderCommand;
import com.newton.zaocycle.rider.domain.model.Rider;
import com.newton.zaocycle.rider.domain.port.RiderRepository;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.domain.Ward;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
class RiderServiceImpl implements RiderService {

    private final RiderRepository repository;
    private final PasswordEncoder passwordEncoder;

    RiderServiceImpl(RiderRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Rider register(RegisterRiderCommand cmd) {
        PhoneNumber phone = PhoneNumber.of(cmd.phone());
        Ward ward = Ward.valueOf(cmd.ward());
        String hash = passwordEncoder.encode(cmd.password());
        return repository.save(Rider.create(phone, cmd.fullName(), ward, hash));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rider> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rider> findByPhone(PhoneNumber phone) {
        return repository.findByPhone(phone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rider> findActiveInWard(Ward ward) {
        return repository.findActiveByWard(ward);
    }

    @Override
    @Transactional
    public Rider deactivate(UUID id) {
        Rider rider = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rider not found: " + id));
        rider.deactivate();
        return repository.save(rider);
    }
}

package com.newton.zaocycle.auth.application;

import com.newton.zaocycle.auth.application.command.CreateStaffCommand;
import com.newton.zaocycle.auth.domain.model.StaffUser;
import com.newton.zaocycle.auth.domain.port.StaffUserRepository;
import com.newton.zaocycle.shared.exception.NotFoundException;
import com.newton.zaocycle.shared.exception.ValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
class StaffServiceImpl implements StaffService {

    private final StaffUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    StaffServiceImpl(StaffUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public StaffUser create(CreateStaffCommand cmd) {
        if (repository.existsByEmail(cmd.email())) {
            throw new ValidationException("Email already registered");
        }
        StaffUser user = StaffUser.create(
                cmd.email(),
                passwordEncoder.encode(cmd.password()),
                cmd.fullName(),
                cmd.role()
        );
        return repository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffUser> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StaffUser> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public StaffUser deactivate(UUID id) {
        StaffUser user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Staff user not found: " + id));
        user.deactivate();
        return repository.save(user);
    }

    @Override
    @Transactional
    public StaffUser reactivate(UUID id) {
        StaffUser user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Staff user not found: " + id));
        user.reactivate();
        return repository.save(user);
    }

    @Override
    @Transactional
    public StaffUser updateProfileImage(UUID id, String imageUrl) {
        StaffUser user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Staff user not found: " + id));
        user.updateProfileImage(imageUrl);
        return repository.save(user);
    }
}

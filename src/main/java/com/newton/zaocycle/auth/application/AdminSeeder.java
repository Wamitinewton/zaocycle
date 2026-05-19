package com.newton.zaocycle.auth.application;

import com.newton.zaocycle.auth.domain.model.Role;
import com.newton.zaocycle.auth.domain.model.StaffUser;
import com.newton.zaocycle.auth.domain.port.StaffUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);
    private static final String SEED_EMAIL = "newtondev461@gmail.com";

    private final StaffUserRepository staffUserRepository;
    private final PasswordEncoder passwordEncoder;

    AdminSeeder(StaffUserRepository staffUserRepository, PasswordEncoder passwordEncoder) {
        this.staffUserRepository = staffUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (staffUserRepository.existsByEmail(SEED_EMAIL)) {
            return;
        }
        StaffUser admin = StaffUser.create(
                SEED_EMAIL,
                passwordEncoder.encode("ZaoCycle@55"),
                "Newton Wamiti",
                Role.ADMIN
        );
        staffUserRepository.save(admin);
        log.info("Seeded default admin: {}", SEED_EMAIL);
    }
}

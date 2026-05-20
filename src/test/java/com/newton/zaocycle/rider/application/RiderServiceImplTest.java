package com.newton.zaocycle.rider.application;

import com.newton.zaocycle.rider.application.command.RegisterRiderCommand;
import com.newton.zaocycle.rider.domain.model.Rider;
import com.newton.zaocycle.rider.domain.port.RiderRepository;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.domain.Ward;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiderServiceImplTest {

    @Mock
    private RiderRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private RiderServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new RiderServiceImpl(repository, passwordEncoder);
    }

    @Test
    void register_hashesPasswordAndSaves() {
        RegisterRiderCommand cmd = new RegisterRiderCommand("+254711000001", "Alice Wanjiku", "MWEA", "secret123");
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-secret");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.register(cmd);

        verify(passwordEncoder).encode("secret123");
        ArgumentCaptor<Rider> captor = ArgumentCaptor.forClass(Rider.class);
        verify(repository).save(captor.capture());
        Rider rider = captor.getValue();
        assertThat(rider.passwordHash()).isEqualTo("hashed-secret");
        assertThat(rider.phone().value()).isEqualTo("+254711000001");
        assertThat(rider.fullName()).isEqualTo("Alice Wanjiku");
        assertThat(rider.assignedWard()).isEqualTo(Ward.MWEA);
        assertThat(rider.isActive()).isTrue();
    }

    @Test
    void deactivate_existingRider_setsActiveFalse() {
        UUID id = UUID.randomUUID();
        Rider rider = new Rider(id, PhoneNumber.of("+254711000002"), "Bob Kamau",
                "hash", Ward.NDIA, true, Instant.now(), Instant.now());
        when(repository.findById(id)).thenReturn(Optional.of(rider));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Rider result = service.deactivate(id);

        assertThat(result.isActive()).isFalse();
        verify(repository).save(rider);
    }

    @Test
    void deactivate_unknownId_throwsNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deactivate(id))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findByPhone_delegatesToRepository() {
        PhoneNumber phone = PhoneNumber.of("+254711000003");
        when(repository.findByPhone(phone)).thenReturn(Optional.empty());

        Optional<Rider> result = service.findByPhone(phone);

        assertThat(result).isEmpty();
        verify(repository).findByPhone(phone);
    }
}

package com.newton.zaocycle.buyer.application;

import com.newton.zaocycle.buyer.application.command.RegisterBuyerCommand;
import com.newton.zaocycle.buyer.application.command.UpdateBuyerCommand;
import com.newton.zaocycle.buyer.domain.model.Buyer;
import com.newton.zaocycle.buyer.domain.model.BuyerType;
import com.newton.zaocycle.buyer.domain.port.BuyerRepository;
import com.newton.zaocycle.shared.exception.NotFoundException;
import com.newton.zaocycle.shared.exception.ValidationException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuyerServiceImplTest {

    @Mock
    private BuyerRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private BuyerServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new BuyerServiceImpl(repository, passwordEncoder);
    }

    @Test
    void register_hashesPasswordAndSaves() {
        RegisterBuyerCommand cmd = new RegisterBuyerCommand(
                "school@test.com", "pass123", "+254700000010",
                BuyerType.SCHOOL, "Test School", null, null, null);

        when(repository.existsByEmail("school@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hashed-pass");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Buyer saved = service.register(cmd);

        verify(passwordEncoder).encode("pass123");
        ArgumentCaptor<Buyer> captor = ArgumentCaptor.forClass(Buyer.class);
        verify(repository).save(captor.capture());
        Buyer buyer = captor.getValue();
        assertThat(buyer.passwordHash()).isEqualTo("hashed-pass");
        assertThat(buyer.email()).isEqualTo("school@test.com");
        assertThat(buyer.buyerType()).isEqualTo(BuyerType.SCHOOL);
        assertThat(buyer.displayName()).isEqualTo("Test School");
        assertThat(buyer.isActive()).isTrue();
    }

    @Test
    void register_duplicateEmail_throwsValidationException() {
        RegisterBuyerCommand cmd = new RegisterBuyerCommand(
                "dup@test.com", "pass123", "+254700000011",
                BuyerType.INDIVIDUAL, "Dup User", null, null, null);

        when(repository.existsByEmail("dup@test.com")).thenReturn(true);

        assertThatThrownBy(() -> service.register(cmd))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email already registered");

        verify(repository, never()).save(any());
    }

    @Test
    void updateProfile_existingBuyer_updatesFields() {
        UUID id = UUID.randomUUID();
        Buyer buyer = new Buyer(id, "a@b.com", "hash", "+254700000012",
                BuyerType.BUSINESS, "Old Name", null, null, null, true,
                Instant.now(), Instant.now());

        when(repository.findById(id)).thenReturn(Optional.of(buyer));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateBuyerCommand cmd = new UpdateBuyerCommand("New Name", "Jane", "Nairobi", "MWEA");
        Buyer updated = service.updateProfile(id, cmd);

        assertThat(updated.displayName()).isEqualTo("New Name");
        assertThat(updated.contactPerson()).isEqualTo("Jane");
        assertThat(updated.address()).isEqualTo("Nairobi");
        assertThat(updated.ward()).isEqualTo("MWEA");
    }

    @Test
    void updateProfile_unknownId_throwsNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProfile(id, new UpdateBuyerCommand("N", null, null, null)))
                .isInstanceOf(NotFoundException.class);
    }
}

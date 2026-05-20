package com.newton.zaocycle.auth.application;

import com.newton.zaocycle.auth.application.command.CreateStaffCommand;
import com.newton.zaocycle.auth.domain.model.Role;
import com.newton.zaocycle.auth.domain.model.StaffUser;
import com.newton.zaocycle.auth.domain.port.StaffUserRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceImplTest {

    @Mock
    private StaffUserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private StaffServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new StaffServiceImpl(repository, passwordEncoder);
    }

    @Test
    void create_hashesPasswordAndSaves() {
        CreateStaffCommand cmd = new CreateStaffCommand(
                "manager@test.com", "secret123", "Jane Doe", Role.COOP_MANAGER);

        when(repository.existsByEmail("manager@test.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-secret");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StaffUser result = service.create(cmd);

        verify(passwordEncoder).encode("secret123");
        ArgumentCaptor<StaffUser> captor = ArgumentCaptor.forClass(StaffUser.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().passwordHash()).isEqualTo("hashed-secret");
        assertThat(captor.getValue().email()).isEqualTo("manager@test.com");
        assertThat(captor.getValue().role()).isEqualTo(Role.COOP_MANAGER);
        assertThat(captor.getValue().isActive()).isTrue();
    }

    @Test
    void create_duplicateEmail_throwsValidation() {
        CreateStaffCommand cmd = new CreateStaffCommand(
                "dup@test.com", "pass123", "Dup User", Role.ADMIN);

        when(repository.existsByEmail("dup@test.com")).thenReturn(true);

        assertThatThrownBy(() -> service.create(cmd))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email already registered");

        verify(repository, never()).save(any());
    }

    @Test
    void findAll_delegatesToRepository() {
        StaffUser user = new StaffUser(UUID.randomUUID(), "a@b.com", "hash",
                "Alice", Role.COOP_MANAGER, true, Instant.now(), Instant.now());
        when(repository.findAll()).thenReturn(List.of(user));

        List<StaffUser> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).email()).isEqualTo("a@b.com");
    }

    @Test
    void deactivate_setsActiveFalse() {
        UUID id = UUID.randomUUID();
        StaffUser user = new StaffUser(id, "a@b.com", "hash",
                "Alice", Role.COOP_MANAGER, true, Instant.now(), Instant.now());

        when(repository.findById(id)).thenReturn(Optional.of(user));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StaffUser result = service.deactivate(id);

        assertThat(result.isActive()).isFalse();
        verify(repository).save(user);
    }

    @Test
    void deactivate_unknownId_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deactivate(id))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void reactivate_setsActiveTrue() {
        UUID id = UUID.randomUUID();
        StaffUser user = new StaffUser(id, "a@b.com", "hash",
                "Alice", Role.ADMIN, false, Instant.now(), Instant.now());

        when(repository.findById(id)).thenReturn(Optional.of(user));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StaffUser result = service.reactivate(id);

        assertThat(result.isActive()).isTrue();
    }

    @Test
    void updateProfileImage_persistsUrl() {
        UUID id = UUID.randomUUID();
        StaffUser user = new StaffUser(id, "a@b.com", "hash",
                "Alice", Role.ADMIN, true, Instant.now(), Instant.now());

        when(repository.findById(id)).thenReturn(Optional.of(user));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StaffUser result = service.updateProfileImage(id, "https://cdn.example.com/img.jpg");

        assertThat(result.profileImageUrl()).isEqualTo("https://cdn.example.com/img.jpg");
        verify(repository).save(user);
    }

    @Test
    void updateProfileImage_unknownId_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProfileImage(id, "https://example.com/img.jpg"))
                .isInstanceOf(NotFoundException.class);
    }
}

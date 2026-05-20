package com.newton.zaocycle.farmer.application;

import com.newton.zaocycle.farmer.application.command.RegisterFarmerCommand;
import com.newton.zaocycle.farmer.application.command.UpdateFarmerLocationCommand;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.farmer.domain.port.FarmerRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FarmerServiceImplTest {

    @Mock private FarmerRepository repository;
    @Mock private PasswordEncoder passwordEncoder;

    private FarmerServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new FarmerServiceImpl(repository, passwordEncoder);
    }

    @Test
    void register_persistsAllLocationFields() {
        PhoneNumber phone = PhoneNumber.of("+254700000001");
        Farmer unregistered = Farmer.newUnregistered(phone);
        when(repository.findByPhone(phone)).thenReturn(Optional.of(unregistered));
        when(passwordEncoder.encode("1234")).thenReturn("hashed");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RegisterFarmerCommand cmd = new RegisterFarmerCommand(
                phone, "Jane Wanjiku", Ward.MWEA, "1234",
                "Mwea Town", -0.6584, 37.3968);

        Farmer result = service.register(cmd);

        assertThat(result.tradingCenter()).isEqualTo("Mwea Town");
        assertThat(result.latitude()).isEqualTo(-0.6584);
        assertThat(result.longitude()).isEqualTo(37.3968);
        assertThat(result.isRegistrationComplete()).isTrue();
        verify(passwordEncoder).encode("1234");
    }

    @Test
    void register_withNullLocation_stillCompletes() {
        PhoneNumber phone = PhoneNumber.of("+254700000002");
        Farmer unregistered = Farmer.newUnregistered(phone);
        when(repository.findByPhone(phone)).thenReturn(Optional.of(unregistered));
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RegisterFarmerCommand cmd = new RegisterFarmerCommand(
                phone, "John Kamau", Ward.NDIA, "5678",
                null, null, null);

        Farmer result = service.register(cmd);

        assertThat(result.isRegistrationComplete()).isTrue();
        assertThat(result.tradingCenter()).isNull();
        assertThat(result.latitude()).isNull();
        assertThat(result.longitude()).isNull();
    }

    @Test
    void register_throwsNotFoundWhenFarmerDoesNotExist() {
        PhoneNumber phone = PhoneNumber.of("+254700000003");
        when(repository.findByPhone(phone)).thenReturn(Optional.empty());

        RegisterFarmerCommand cmd = new RegisterFarmerCommand(
                phone, "Ghost User", Ward.GICHUGU, "0000",
                "Kerugoya", -0.4993, 37.2768);

        assertThatThrownBy(() -> service.register(cmd))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateLocation_updatesAllFieldsAndSaves() {
        UUID farmerId = UUID.randomUUID();
        Farmer farmer = registeredFarmer(farmerId);
        when(repository.findById(farmerId)).thenReturn(Optional.of(farmer));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateFarmerLocationCommand cmd = new UpdateFarmerLocationCommand(
                farmerId, "Sagana", -0.6705, 37.2084);

        Farmer result = service.updateLocation(cmd);

        assertThat(result.tradingCenter()).isEqualTo("Sagana");
        assertThat(result.latitude()).isEqualTo(-0.6705);
        assertThat(result.longitude()).isEqualTo(37.2084);

        ArgumentCaptor<Farmer> captor = ArgumentCaptor.forClass(Farmer.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().tradingCenter()).isEqualTo("Sagana");
    }

    @Test
    void updateLocation_throwsNotFoundForUnknownFarmer() {
        UUID farmerId = UUID.randomUUID();
        when(repository.findById(farmerId)).thenReturn(Optional.empty());

        UpdateFarmerLocationCommand cmd = new UpdateFarmerLocationCommand(
                farmerId, "Kutus", -0.5330, 37.2650);

        assertThatThrownBy(() -> service.updateLocation(cmd))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findOrCreateByPhone_createsAndSavesWhenAbsent() {
        PhoneNumber phone = PhoneNumber.of("+254700000004");
        when(repository.findByPhone(phone)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Farmer result = service.findOrCreateByPhone(phone);

        assertThat(result.phone()).isEqualTo(phone);
        assertThat(result.isRegistrationComplete()).isFalse();
        verify(repository).save(any(Farmer.class));
    }

    @Test
    void findOrCreateByPhone_returnsExistingWithoutSaving() {
        PhoneNumber phone = PhoneNumber.of("+254700000005");
        Farmer existing = Farmer.newUnregistered(phone);
        when(repository.findByPhone(phone)).thenReturn(Optional.of(existing));

        Farmer result = service.findOrCreateByPhone(phone);

        assertThat(result).isSameAs(existing);
        verify(repository, never()).save(any());
    }

    private Farmer registeredFarmer(UUID id) {
        Instant now = Instant.now();
        return new Farmer(id, PhoneNumber.of("+254711999001"),
                "Alice Mwangi", Ward.MWEA, "hashed",
                true, "Mwea Town", -0.6584, 37.3968,
                null, now, now);
    }
}

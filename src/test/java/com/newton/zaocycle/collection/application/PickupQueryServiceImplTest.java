package com.newton.zaocycle.collection.application;

import com.newton.zaocycle.collection.domain.model.FarmerEarnings;
import com.newton.zaocycle.collection.domain.model.PickupStatus;
import com.newton.zaocycle.collection.domain.model.WastePickup;
import com.newton.zaocycle.collection.domain.port.PickupRepository;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PickupQueryServiceImplTest {

    @Mock private PickupRepository pickupRepo;

    private PickupQueryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PickupQueryServiceImpl(pickupRepo);
    }

    @Test
    void findById_throwsNotFoundWhenMissing() {
        UUID id = UUID.randomUUID();
        when(pickupRepo.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getFarmerEarnings_assembliesCorrectly() {
        UUID farmerId = UUID.randomUUID();
        WastePickup paid = paidPickup(farmerId);

        when(pickupRepo.sumPayoutsByFarmer(farmerId)).thenReturn(BigDecimal.valueOf(500));
        when(pickupRepo.sumPayoutsByFarmerSince(eq(farmerId), any(Instant.class))).thenReturn(BigDecimal.valueOf(100));
        when(pickupRepo.countPaidByFarmer(farmerId)).thenReturn(5L);
        when(pickupRepo.findRecentPaidByFarmer(farmerId, 3)).thenReturn(List.of(paid));

        FarmerEarnings earnings = service.getFarmerEarnings(farmerId);

        assertThat(earnings.total()).isEqualByComparingTo("500");
        assertThat(earnings.thisMonth()).isEqualByComparingTo("100");
        assertThat(earnings.pickupCount()).isEqualTo(5);
        assertThat(earnings.recentPayouts()).hasSize(1);
    }

    @Test
    void hasOpenPickup_returnsTrueWhenExists() {
        UUID farmerId = UUID.randomUUID();
        when(pickupRepo.existsByFarmerIdAndStatusIn(eq(farmerId),
                eq(List.of(PickupStatus.REQUESTED, PickupStatus.ASSIGNED)))).thenReturn(true);
        assertThat(service.hasOpenPickup(farmerId)).isTrue();
    }

    private WastePickup paidPickup(UUID farmerId) {
        WastePickup pickup = WastePickup.request(farmerId, null, LocalDate.now());
        pickup.assign(UUID.randomUUID());
        pickup.markCollected(BigDecimal.TEN, null, null, BigDecimal.valueOf(50));
        pickup.markPaid(UUID.randomUUID());
        return pickup;
    }
}

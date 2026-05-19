package com.newton.zaocycle.collection.application;

import com.newton.zaocycle.collection.domain.model.WastePickup;
import com.newton.zaocycle.collection.domain.port.PickupRepository;
import com.newton.zaocycle.rider.application.RiderService;
import com.newton.zaocycle.rider.domain.model.Rider;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.domain.Ward;
import com.newton.zaocycle.shared.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiderAssignmentServiceTest {

    @Mock private RiderService riderService;
    @Mock private PickupRepository pickupRepo;

    private RiderAssignmentService service;

    @BeforeEach
    void setUp() {
        service = new RiderAssignmentService(riderService, pickupRepo);
    }

    private Rider rider(UUID id) {
        return new Rider(id, PhoneNumber.of("+254700000001"), "Rider One",
                "hash", Ward.MWEA, true, Instant.now(), Instant.now());
    }

    @Test
    void findLeastLoadedRider_returnsRiderWithFewestPickups() {
        UUID riderA = UUID.randomUUID();
        UUID riderB = UUID.randomUUID();
        Rider a = rider(riderA);
        Rider b = rider(riderB);

        when(riderService.findActiveInWard(Ward.MWEA)).thenReturn(List.of(a, b));
        when(pickupRepo.findByRiderIdAndScheduledFor(eq(riderA), any(LocalDate.class)))
                .thenReturn(List.of(mockPickup(), mockPickup()));
        when(pickupRepo.findByRiderIdAndScheduledFor(eq(riderB), any(LocalDate.class)))
                .thenReturn(List.of(mockPickup()));

        UUID result = service.findLeastLoadedRiderInWard(Ward.MWEA);
        assertThat(result).isEqualTo(riderB);
    }

    @Test
    void findLeastLoadedRider_throwsWhenNoActiveRiders() {
        when(riderService.findActiveInWard(Ward.MWEA)).thenReturn(List.of());
        assertThatThrownBy(() -> service.findLeastLoadedRiderInWard(Ward.MWEA))
                .isInstanceOf(ValidationException.class);
    }

    private WastePickup mockPickup() {
        return WastePickup.request(UUID.randomUUID(), null, LocalDate.now().plusDays(1));
    }
}

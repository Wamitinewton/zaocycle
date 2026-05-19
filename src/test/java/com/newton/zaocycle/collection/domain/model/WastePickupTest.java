package com.newton.zaocycle.collection.domain.model;

import com.newton.zaocycle.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WastePickupTest {

    private static WastePickup requested() {
        return WastePickup.request(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now().plusDays(1));
    }

    @Test
    void request_createsPickupWithRequestedStatus() {
        WastePickup pickup = requested();
        assertThat(pickup.status()).isEqualTo(PickupStatus.REQUESTED);
        assertThat(pickup.id()).isNotNull();
        assertThat(pickup.riderId()).isNull();
    }

    @Test
    void assign_transitionsToAssigned() {
        WastePickup pickup = requested();
        UUID riderId = UUID.randomUUID();
        pickup.assign(riderId);
        assertThat(pickup.status()).isEqualTo(PickupStatus.ASSIGNED);
        assertThat(pickup.riderId()).isEqualTo(riderId);
    }

    @Test
    void assign_throwsWhenNotRequested() {
        WastePickup pickup = requested();
        pickup.assign(UUID.randomUUID());
        assertThatThrownBy(() -> pickup.assign(UUID.randomUUID()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void markCollected_transitionsToCollected() {
        WastePickup pickup = requested();
        pickup.assign(UUID.randomUUID());
        pickup.markCollected(BigDecimal.valueOf(10), "http://photo.jpg", "notes", BigDecimal.valueOf(50));
        assertThat(pickup.status()).isEqualTo(PickupStatus.COLLECTED);
        assertThat(pickup.weightKg()).isEqualByComparingTo("10");
        assertThat(pickup.payoutAmount()).isEqualByComparingTo("50");
        assertThat(pickup.collectedAt()).isNotNull();
    }

    @Test
    void markCollected_throwsWhenNotAssigned() {
        WastePickup pickup = requested();
        assertThatThrownBy(() ->
                pickup.markCollected(BigDecimal.TEN, null, null, BigDecimal.TEN))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void markPaid_transitionsToPaid() {
        WastePickup pickup = requested();
        pickup.assign(UUID.randomUUID());
        pickup.markCollected(BigDecimal.TEN, null, null, BigDecimal.TEN);
        UUID txId = UUID.randomUUID();
        pickup.markPaid(txId);
        assertThat(pickup.status()).isEqualTo(PickupStatus.PAID);
        assertThat(pickup.mpesaTransactionId()).isEqualTo(txId);
        assertThat(pickup.paidAt()).isNotNull();
    }

    @Test
    void markPaid_throwsWhenNotCollected() {
        WastePickup pickup = requested();
        pickup.assign(UUID.randomUUID());
        assertThatThrownBy(() -> pickup.markPaid(UUID.randomUUID()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void cancel_fromRequested_transitions() {
        WastePickup pickup = requested();
        pickup.cancel();
        assertThat(pickup.status()).isEqualTo(PickupStatus.CANCELLED);
    }

    @Test
    void cancel_fromAssigned_transitions() {
        WastePickup pickup = requested();
        pickup.assign(UUID.randomUUID());
        pickup.cancel();
        assertThat(pickup.status()).isEqualTo(PickupStatus.CANCELLED);
    }

    @Test
    void cancel_throwsWhenCollected() {
        WastePickup pickup = requested();
        pickup.assign(UUID.randomUUID());
        pickup.markCollected(BigDecimal.TEN, null, null, BigDecimal.TEN);
        assertThatThrownBy(pickup::cancel).isInstanceOf(ValidationException.class);
    }

    @Test
    void fail_setsFailedStatus() {
        WastePickup pickup = requested();
        pickup.fail();
        assertThat(pickup.status()).isEqualTo(PickupStatus.FAILED);
    }
}

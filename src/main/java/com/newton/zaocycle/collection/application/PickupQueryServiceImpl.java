package com.newton.zaocycle.collection.application;

import com.newton.zaocycle.collection.domain.model.FarmerEarnings;
import com.newton.zaocycle.collection.domain.model.PayoutEntry;
import com.newton.zaocycle.collection.domain.model.PickupStatus;
import com.newton.zaocycle.collection.domain.model.WastePickup;
import com.newton.zaocycle.collection.domain.port.PickupRepository;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
class PickupQueryServiceImpl implements PickupQueryService {

    private static final ZoneId NAIROBI = ZoneId.of("Africa/Nairobi");

    private final PickupRepository pickupRepo;

    PickupQueryServiceImpl(PickupRepository pickupRepo) {
        this.pickupRepo = pickupRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WastePickup> findByFarmer(UUID farmerId) {
        return pickupRepo.findByFarmerId(farmerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WastePickup> findForRiderToday(UUID riderId) {
        LocalDate today = LocalDate.now(NAIROBI);
        return pickupRepo.findByRiderIdAndScheduledFor(riderId, today);
    }

    @Override
    @Transactional(readOnly = true)
    public WastePickup findById(UUID id) {
        return pickupRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Pickup not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerEarnings getFarmerEarnings(UUID farmerId) {
        Instant monthStart = LocalDate.now(NAIROBI).withDayOfMonth(1)
                .atStartOfDay(NAIROBI).toInstant();
        var total = pickupRepo.sumPayoutsByFarmer(farmerId);
        var thisMonth = pickupRepo.sumPayoutsByFarmerSince(farmerId, monthStart);
        long count = pickupRepo.countPaidByFarmer(farmerId);
        List<PayoutEntry> recent = pickupRepo.findRecentPaidByFarmer(farmerId, 3).stream()
                .map(p -> new PayoutEntry(p.payoutAmount(),
                        p.paidAt().atZone(NAIROBI).toLocalDate()))
                .toList();
        return new FarmerEarnings(total, thisMonth, count, recent);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasOpenPickup(UUID farmerId) {
        return pickupRepo.existsByFarmerIdAndStatusIn(
                farmerId, List.of(PickupStatus.REQUESTED, PickupStatus.ASSIGNED));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WastePickup> search(PickupFilter filter, Pageable pageable) {
        return pickupRepo.search(filter, pageable);
    }
}

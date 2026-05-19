package com.newton.zaocycle.collection.application;

import com.newton.zaocycle.collection.domain.port.PickupRepository;
import com.newton.zaocycle.rider.application.RiderService;
import com.newton.zaocycle.rider.domain.model.Rider;
import com.newton.zaocycle.shared.domain.Ward;
import com.newton.zaocycle.shared.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class RiderAssignmentService {

    private final RiderService riderService;
    private final PickupRepository pickupRepo;

    public RiderAssignmentService(RiderService riderService, PickupRepository pickupRepo) {
        this.riderService = riderService;
        this.pickupRepo = pickupRepo;
    }

    public UUID findLeastLoadedRiderInWard(Ward ward) {
        List<Rider> active = riderService.findActiveInWard(ward);
        if (active.isEmpty()) {
            throw new ValidationException("No active riders in ward: " + ward.displayName());
        }

        LocalDate today = LocalDate.now(ZoneId.of("Africa/Nairobi"));
        return active.stream()
                .min(Comparator.comparingInt(r ->
                        pickupRepo.findByRiderIdAndScheduledFor(r.id(), today).size()))
                .map(Rider::id)
                .orElseThrow();
    }
}

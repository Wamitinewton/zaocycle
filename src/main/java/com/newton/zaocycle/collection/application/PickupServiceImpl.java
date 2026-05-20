package com.newton.zaocycle.collection.application;

import com.newton.zaocycle.collection.application.command.CollectPickupCommand;
import com.newton.zaocycle.collection.application.command.RequestPickupCommand;
import com.newton.zaocycle.collection.application.event.PickupScheduled;
import com.newton.zaocycle.collection.application.event.WasteCollected;
import com.newton.zaocycle.collection.domain.model.WastePickup;
import com.newton.zaocycle.collection.domain.port.PickupPhotoStore;
import com.newton.zaocycle.collection.domain.port.PickupRepository;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.rider.application.RiderService;
import com.newton.zaocycle.rider.domain.model.Rider;
import com.newton.zaocycle.scheduling.application.SchedulingService;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
class PickupServiceImpl implements PickupService {

    private final PickupRepository pickupRepo;
    private final FarmerService farmerService;
    private final RiderService riderService;
    private final RiderAssignmentService assignmentService;
    private final SchedulingService schedulingService;
    private final PickupPhotoStore photoStore;
    private final ApplicationEventPublisher eventPublisher;
    private final BigDecimal payoutRatePerKg;

    PickupServiceImpl(PickupRepository pickupRepo,
                      FarmerService farmerService,
                      RiderService riderService,
                      RiderAssignmentService assignmentService,
                      SchedulingService schedulingService,
                      PickupPhotoStore photoStore,
                      ApplicationEventPublisher eventPublisher,
                      @Value("${zaocycle.payment.rate-per-kg:5.00}") BigDecimal payoutRatePerKg) {
        this.pickupRepo = pickupRepo;
        this.farmerService = farmerService;
        this.riderService = riderService;
        this.assignmentService = assignmentService;
        this.schedulingService = schedulingService;
        this.photoStore = photoStore;
        this.eventPublisher = eventPublisher;
        this.payoutRatePerKg = payoutRatePerKg;
    }

    @Override
    @Transactional
    public WastePickup requestPickup(RequestPickupCommand cmd) {
        WastePickup pickup = WastePickup.request(cmd.farmerId(), cmd.applicationId(), cmd.scheduledFor());
        return pickupRepo.save(pickup);
    }

    @Override
    @Transactional
    public WastePickup assignRider(UUID pickupId, UUID riderId) {
        WastePickup pickup = loadOrThrow(pickupId);
        pickup.assign(riderId);
        WastePickup saved = pickupRepo.save(pickup);
        publishPickupScheduled(saved);
        return saved;
    }

    @Override
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public WastePickup autoAssign(UUID pickupId) {
        WastePickup pickup = loadOrThrow(pickupId);
        Farmer farmer = farmerService.findById(pickup.farmerId())
                .orElseThrow(() -> new NotFoundException("Farmer not found: " + pickup.farmerId()));
        UUID riderId = assignmentService.findLeastLoadedRiderInWard(farmer.ward());
        pickup.assign(riderId);
        WastePickup saved = pickupRepo.save(pickup);
        publishPickupScheduled(saved);
        return saved;
    }

    @Override
    @Transactional
    public WastePickup markCollected(UUID pickupId, CollectPickupCommand cmd) {
        WastePickup pickup = loadOrThrow(pickupId);
        String photoUrl = null;
        if (cmd.photo() != null && cmd.photo().length > 0) {
            String filename = "pickup-" + pickupId + "-" + System.currentTimeMillis() + ".jpg";
            photoUrl = photoStore.upload(filename, cmd.photo(), "image/jpeg");
        }
        BigDecimal payout = cmd.weightKg().multiply(payoutRatePerKg);
        pickup.markCollected(cmd.weightKg(), photoUrl, cmd.notes(), payout);
        WastePickup saved = pickupRepo.save(pickup);
        schedulingService.cancelPickupJobs(pickupId);
        eventPublisher.publishEvent(new WasteCollected(
                saved.id(), saved.farmerId(), saved.riderId(),
                saved.weightKg(), saved.payoutAmount()));
        return saved;
    }

    @Override
    @Transactional
    public WastePickup markPaid(UUID pickupId, UUID mpesaTxId) {
        WastePickup pickup = loadOrThrow(pickupId);
        pickup.markPaid(mpesaTxId);
        return pickupRepo.save(pickup);
    }

    @Override
    @Transactional
    public void reassignExpired(UUID pickupId) {
        WastePickup pickup = loadOrThrow(pickupId);
        try {
            Farmer farmer = farmerService.findById(pickup.farmerId())
                    .orElseThrow(() -> new NotFoundException("Farmer not found: " + pickup.farmerId()));
            UUID newRiderId = assignmentService.findLeastLoadedRiderInWard(farmer.ward());
            pickup.assign(newRiderId);
            WastePickup saved = pickupRepo.save(pickup);
            publishPickupScheduled(saved);
        } catch (Exception e) {
            pickup.fail();
            pickupRepo.save(pickup);
        }
    }

    @Override
    @Transactional
    public void cancel(UUID pickupId) {
        WastePickup pickup = loadOrThrow(pickupId);
        pickup.cancel();
        pickupRepo.save(pickup);
    }

    private WastePickup loadOrThrow(UUID pickupId) {
        return pickupRepo.findById(pickupId)
                .orElseThrow(() -> new NotFoundException("Pickup not found: " + pickupId));
    }

    private void publishPickupScheduled(WastePickup pickup) {
        Farmer farmer = farmerService.findById(pickup.farmerId()).orElse(null);
        Rider rider = pickup.riderId() != null
                ? riderService.findById(pickup.riderId()).orElse(null)
                : null;
        eventPublisher.publishEvent(new PickupScheduled(
                pickup.id(),
                pickup.farmerId(),
                pickup.riderId(),
                pickup.scheduledFor(),
                farmer != null ? farmer.phone().value() : null,
                rider != null ? rider.phone().value() : null,
                farmer != null ? farmer.fullName() : null,
                rider != null ? rider.fullName() : null,
                farmer != null && farmer.ward() != null ? farmer.ward().displayName() : null
        ));
    }
}

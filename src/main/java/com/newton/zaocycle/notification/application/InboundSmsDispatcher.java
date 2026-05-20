package com.newton.zaocycle.notification.application;

import com.newton.zaocycle.collection.application.PickupQueryService;
import com.newton.zaocycle.collection.application.event.WastePickupRequested;
import com.newton.zaocycle.collection.domain.model.FarmerEarnings;
import com.newton.zaocycle.collection.domain.model.PickupSource;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.notification.domain.model.InboundSms;
import com.newton.zaocycle.notification.domain.port.InboundSmsRepository;
import com.newton.zaocycle.pesticide.application.PesticideApplicationService;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InboundSmsDispatcher {

    private static final Logger log = LoggerFactory.getLogger(InboundSmsDispatcher.class);

    private final FarmerService farmerService;
    private final NotificationService notifications;
    private final InboundSmsRepository inboundRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final PickupQueryService pickupQueryService;
    private final PesticideApplicationService pesticideService;

    public InboundSmsDispatcher(FarmerService farmerService,
                                 NotificationService notifications,
                                 InboundSmsRepository inboundRepo,
                                 ApplicationEventPublisher eventPublisher,
                                 PickupQueryService pickupQueryService,
                                 PesticideApplicationService pesticideService) {
        this.farmerService = farmerService;
        this.notifications = notifications;
        this.inboundRepo = inboundRepo;
        this.eventPublisher = eventPublisher;
        this.pickupQueryService = pickupQueryService;
        this.pesticideService = pesticideService;
    }

    @Transactional
    public void handle(String phone, String body) {
        log.info("Inbound SMS: phone={} body=[{}]", phone, body);
        InboundSms record = InboundSms.received(phone, body);
        String command = parseCommand(body);
        record.setCommandParsed(command);
        log.debug("Inbound SMS parsed: phone={} command={}", phone, command);

        switch (command) {
            case "WASTE" -> handleWaste(phone, record);
            case "BAL"   -> handleBalance(phone, record);
            default      -> handleUnknown(phone, record);
        }
        inboundRepo.save(record);
    }

    private String parseCommand(String body) {
        if (body == null) return "UNKNOWN";
        String trimmed = body.trim().toUpperCase();
        if (trimmed.startsWith("WASTE")) return "WASTE";
        if (trimmed.startsWith("BAL")) return "BAL";
        return "UNKNOWN";
    }

    private void handleWaste(String phone, InboundSms record) {
        Optional<Farmer> farmer = farmerService.findByPhone(PhoneNumber.of(phone));
        if (farmer.isEmpty()) {
            log.warn("WASTE from unregistered phone={}", phone);
            notifications.sendTemplated(phone, "NOT_REGISTERED", Map.of());
            record.markRejected("Farmer not registered");
            return;
        }

        List<PesticideApplication> readyApps = pesticideService.findReadyForPickup(farmer.get().id());
        if (readyApps.isEmpty()) {
            log.warn("WASTE from farmer={} with no certified crops ready", farmer.get().id());
            notifications.sendTemplated(phone, "NOT_HARVEST_READY", Map.of());
            record.markRejected("No certified crops ready for pickup");
            return;
        }

        PesticideApplication app = readyApps.stream()
                .max(java.util.Comparator.comparing(PesticideApplication::safeHarvestDate))
                .get();

        log.info("WASTE pickup requested: phone={} farmerId={} applicationId={} crop={}",
                phone, farmer.get().id(), app.id(), app.crop());
        eventPublisher.publishEvent(new WastePickupRequested(farmer.get().id(), app.id(), PickupSource.SMS));
        record.markProcessed();
    }

    private void handleBalance(String phone, InboundSms record) {
        Optional<Farmer> farmer = farmerService.findByPhone(PhoneNumber.of(phone));
        if (farmer.isEmpty()) {
            notifications.sendTemplated(phone, "NOT_REGISTERED", Map.of());
            record.markRejected("Farmer not registered");
            return;
        }
        FarmerEarnings earnings = pickupQueryService.getFarmerEarnings(farmer.get().id());
        String msg = String.format(
            "ZaoCycle: Total KES %s | This month KES %s | %d pickups. Reply WASTE to schedule.",
            earnings.total().stripTrailingZeros().toPlainString(),
            earnings.thisMonth().stripTrailingZeros().toPlainString(),
            earnings.pickupCount()
        );
        notifications.sendRaw(phone, msg);
        record.markProcessed();
    }

    private void handleUnknown(String phone, InboundSms record) {
        notifications.sendTemplated(phone, "UNKNOWN_COMMAND", Map.of());
        record.markProcessed();
    }
}

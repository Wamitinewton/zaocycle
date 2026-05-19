package com.newton.zaocycle.notification.application;

import com.newton.zaocycle.collection.application.event.WastePickupRequested;
import com.newton.zaocycle.collection.domain.model.PickupSource;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.notification.domain.model.InboundSms;
import com.newton.zaocycle.notification.domain.port.InboundSmsRepository;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class InboundSmsDispatcher {

    private final FarmerService farmerService;
    private final NotificationService notifications;
    private final InboundSmsRepository inboundRepo;
    private final ApplicationEventPublisher eventPublisher;

    public InboundSmsDispatcher(FarmerService farmerService,
                                 NotificationService notifications,
                                 InboundSmsRepository inboundRepo,
                                 ApplicationEventPublisher eventPublisher) {
        this.farmerService = farmerService;
        this.notifications = notifications;
        this.inboundRepo = inboundRepo;
        this.eventPublisher = eventPublisher;
    }

    public void handle(String phone, String body) {
        InboundSms record = InboundSms.received(phone, body);
        String command = parseCommand(body);
        record.setCommandParsed(command);

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
            notifications.sendTemplated(phone, "NOT_REGISTERED", Map.of());
            record.markRejected("Farmer not registered");
            return;
        }
        eventPublisher.publishEvent(new WastePickupRequested(farmer.get().id(), null, PickupSource.SMS));

        record.markProcessed();
    }

    private void handleBalance(String phone, InboundSms record) {
        // TODO: implement once PickupQueryService is available in WORKFLOW-08
        notifications.sendRaw(phone, "Reply WASTE to schedule pickup or dial *XXX# for your balance. - ZaoCycle");
        record.markProcessed();
    }

    private void handleUnknown(String phone, InboundSms record) {
        notifications.sendTemplated(phone, "UNKNOWN_COMMAND", Map.of());
        record.markProcessed();
    }
}

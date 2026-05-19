package com.newton.zaocycle.notification.application;

import com.newton.zaocycle.collection.application.event.WastePickupRequested;
import com.newton.zaocycle.collection.domain.model.PickupSource;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.notification.domain.model.InboundSms;
import com.newton.zaocycle.notification.domain.port.InboundSmsRepository;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.domain.Ward;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InboundSmsDispatcherTest {

    @Mock private FarmerService farmerService;
    @Mock private NotificationService notifications;
    @Mock private InboundSmsRepository inboundRepo;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private InboundSmsDispatcher dispatcher;

    private static final String PHONE = "+254700000001";
    private static final UUID FARMER_ID = UUID.randomUUID();

    @Test
    void handle_wasteCommand_publishesWastePickupRequestedEvent() {
        Farmer farmer = new Farmer(FARMER_ID, PhoneNumber.of(PHONE), "John Doe",
                Ward.MWEA, null, true, Instant.now(), Instant.now());
        when(farmerService.findByPhone(PhoneNumber.of(PHONE))).thenReturn(Optional.of(farmer));
        when(inboundRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        dispatcher.handle(PHONE, "WASTE");

        ArgumentCaptor<WastePickupRequested> captor = ArgumentCaptor.forClass(WastePickupRequested.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().farmerId()).isEqualTo(FARMER_ID);
        assertThat(captor.getValue().source()).isEqualTo(PickupSource.SMS);
    }

    @Test
    void handle_wasteCommand_unregisteredFarmer_sendsNotRegisteredSms() {
        when(farmerService.findByPhone(PhoneNumber.of(PHONE))).thenReturn(Optional.empty());
        when(inboundRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        dispatcher.handle(PHONE, "WASTE");

        verify(notifications).sendTemplated(eq(PHONE), eq("NOT_REGISTERED"), anyMap());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void handle_unknownCommand_sendsUnknownCommandSms() {
        when(inboundRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        dispatcher.handle(PHONE, "HELLO");

        verify(notifications).sendTemplated(eq(PHONE), eq("UNKNOWN_COMMAND"), eq(Map.of()));
    }

    @Test
    void handle_persistsInboundRecord() {
        when(inboundRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        dispatcher.handle(PHONE, "HELLO");

        ArgumentCaptor<InboundSms> captor = ArgumentCaptor.forClass(InboundSms.class);
        verify(inboundRepo).save(captor.capture());
        assertThat(captor.getValue().phone()).isEqualTo(PHONE);
        assertThat(captor.getValue().commandParsed()).isEqualTo("UNKNOWN");
    }
}

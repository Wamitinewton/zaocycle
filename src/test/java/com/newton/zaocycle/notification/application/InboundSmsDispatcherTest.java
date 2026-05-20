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
import com.newton.zaocycle.pesticide.domain.model.ApplicationStatus;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.domain.Ward;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InboundSmsDispatcherTest {

    private static final String PHONE = "+254700000001";
    private static final UUID FARMER_ID = UUID.randomUUID();
    private static final UUID APP_ID = UUID.randomUUID();
    @Mock
    private FarmerService farmerService;
    @Mock
    private NotificationService notifications;
    @Mock
    private InboundSmsRepository inboundRepo;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private PickupQueryService pickupQueryService;
    @Mock
    private PesticideApplicationService pesticideService;
    @InjectMocks
    private InboundSmsDispatcher dispatcher;

    private PesticideApplication safeApp() {
        return new PesticideApplication(APP_ID, FARMER_ID, UUID.randomUUID(), "Maize",
                500.0, Instant.now(), LocalDate.now(), ApplicationStatus.SAFE, "USSD",
                Instant.now(), Instant.now());
    }

    @Test
    void handle_wasteCommand_publishesWastePickupRequestedEvent() {
        Farmer farmer = new Farmer(FARMER_ID, PhoneNumber.of(PHONE), "John Doe",
                Ward.MWEA, null, true, null, null, null, null, Instant.now(), Instant.now());
        when(farmerService.findByPhone(PhoneNumber.of(PHONE))).thenReturn(Optional.of(farmer));
        when(pesticideService.findReadyForPickup(FARMER_ID)).thenReturn(List.of(safeApp()));
        when(inboundRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        dispatcher.handle(PHONE, "WASTE");

        ArgumentCaptor<WastePickupRequested> captor = ArgumentCaptor.forClass(WastePickupRequested.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().farmerId()).isEqualTo(FARMER_ID);
        assertThat(captor.getValue().applicationId()).isEqualTo(APP_ID);
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
    void handle_wasteCommand_noCertifiedCrops_sendsNotHarvestReadySms() {
        Farmer farmer = new Farmer(FARMER_ID, PhoneNumber.of(PHONE), "John Doe",
                Ward.MWEA, null, true, null, null, null, null, Instant.now(), Instant.now());
        when(farmerService.findByPhone(PhoneNumber.of(PHONE))).thenReturn(Optional.of(farmer));
        when(pesticideService.findReadyForPickup(FARMER_ID)).thenReturn(List.of());
        when(inboundRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        dispatcher.handle(PHONE, "WASTE");

        verify(notifications).sendTemplated(eq(PHONE), eq("NOT_HARVEST_READY"), anyMap());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void handle_unknownCommand_sendsUnknownCommandSms() {
        when(inboundRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        dispatcher.handle(PHONE, "HELLO");

        verify(notifications).sendTemplated(eq(PHONE), eq("UNKNOWN_COMMAND"), eq(Map.of()));
    }

    @Test
    void handle_balCommand_registeredFarmer_sendsEarningsSms() {
        Farmer farmer = new Farmer(FARMER_ID, PhoneNumber.of(PHONE), "Jane Doe",
                Ward.MWEA, null, true, null, null, null, null, Instant.now(), Instant.now());
        FarmerEarnings earnings = new FarmerEarnings(
                new BigDecimal("1500.00"), new BigDecimal("300.00"), 5L, List.of());
        when(farmerService.findByPhone(PhoneNumber.of(PHONE))).thenReturn(Optional.of(farmer));
        when(pickupQueryService.getFarmerEarnings(FARMER_ID)).thenReturn(earnings);
        when(inboundRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        dispatcher.handle(PHONE, "BAL");

        verify(notifications).sendRaw(eq(PHONE), contains("1500"));
        verify(notifications).sendRaw(eq(PHONE), contains("300"));
        verify(notifications).sendRaw(eq(PHONE), contains("5"));
    }

    @Test
    void handle_balCommand_unregisteredFarmer_sendsNotRegisteredSms() {
        when(farmerService.findByPhone(PhoneNumber.of(PHONE))).thenReturn(Optional.empty());
        when(inboundRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        dispatcher.handle(PHONE, "BAL");

        verify(notifications).sendTemplated(eq(PHONE), eq("NOT_REGISTERED"), anyMap());
        verify(notifications, never()).sendRaw(any(), any());
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

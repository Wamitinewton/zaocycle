package com.newton.zaocycle.payment.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newton.zaocycle.payment.application.event.OrderPaymentFailed;
import com.newton.zaocycle.payment.application.event.PayoutCompleted;
import com.newton.zaocycle.payment.application.event.PayoutFailed;
import com.newton.zaocycle.payment.domain.model.MpesaTransaction;
import com.newton.zaocycle.payment.domain.model.TransactionStatus;
import com.newton.zaocycle.payment.domain.model.TransactionType;
import com.newton.zaocycle.payment.domain.port.MpesaTransactionRepository;
import com.newton.zaocycle.payment.domain.port.PayHeroClient;
import com.newton.zaocycle.payment.infrastructure.payhero.PayHeroProperties;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroStkCallback;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroStkPushResponse;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroWithdrawCallback;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroWithdrawResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayHeroServiceImplTest {

    @Mock
    private MpesaTransactionRepository txRepo;
    @Mock
    private PayHeroClient payHeroClient;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private PayHeroServiceImpl service;

    @BeforeEach
    void setUp() {
        PayHeroProperties props = new PayHeroProperties(
                "https://backend.payhero.co.ke",
                "Basic dGVzdA==",
                4240,
                "https://example.com/payhero/stk/callback",
                "https://example.com/payhero/withdraw/callback");
        lenient().when(txRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        service = new PayHeroServiceImpl(txRepo, payHeroClient, props, eventPublisher, new ObjectMapper());
    }

    @Test
    void initiateB2C_markProcessing_whenWithdrawSucceeds() {
        UUID pickupId = UUID.randomUUID();
        when(payHeroClient.withdraw(any()))
                .thenReturn(new PayHeroWithdrawResponse(true, "ZC-B2C-xxx", "TXN123"));

        MpesaTransaction tx = service.initiateB2C(pickupId, "+254700000001", BigDecimal.valueOf(50));

        verify(txRepo, times(2)).save(any());
        assertThat(tx.status()).isEqualTo(TransactionStatus.PROCESSING);
        assertThat(tx.relatedPickupId()).isEqualTo(pickupId);
    }

    @Test
    void initiateB2C_marksFailed_whenPayHeroThrows() {
        when(payHeroClient.withdraw(any())).thenThrow(new RuntimeException("network error"));

        MpesaTransaction tx = service.initiateB2C(UUID.randomUUID(), "+254700000001", BigDecimal.TEN);

        assertThat(tx.status()).isEqualTo(TransactionStatus.FAILED);
        verify(txRepo, times(2)).save(any());
    }

    @Test
    void initiateStkPush_markProcessing_whenPushSucceeds() {
        UUID orderId = UUID.randomUUID();
        when(payHeroClient.stkPush(any()))
                .thenReturn(new PayHeroStkPushResponse(true, "ws_CO_abc123", "MERCH-001"));

        MpesaTransaction tx = service.initiateStkPush(orderId, "+254700000001", BigDecimal.valueOf(100), "ZC-ORDER");

        assertThat(tx.status()).isEqualTo(TransactionStatus.PROCESSING);
        assertThat(tx.conversationId()).isEqualTo("ws_CO_abc123");
    }

    @Test
    void handleWithdrawCallback_success_publishesPayoutCompleted() {
        UUID pickupId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        MpesaTransaction storedTx = buildProcessingTx(txId, pickupId, "ZC-B2C-ref");
        when(txRepo.findByOriginatorConversationId("ZC-B2C-ref")).thenReturn(Optional.of(storedTx));

        PayHeroWithdrawCallback callback = new PayHeroWithdrawCallback(
                "SUCCESS", "ZC-B2C-ref", BigDecimal.valueOf(50), "0700000001", "TXN999");
        service.handleWithdrawCallback(callback);

        ArgumentCaptor<PayoutCompleted> captor = ArgumentCaptor.forClass(PayoutCompleted.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().pickupId()).isEqualTo(pickupId);
        assertThat(captor.getValue().mpesaTransactionId()).isEqualTo(txId);
    }

    @Test
    void handleWithdrawCallback_failure_publishesPayoutFailed() {
        UUID pickupId = UUID.randomUUID();
        MpesaTransaction storedTx = buildProcessingTx(UUID.randomUUID(), pickupId, "ZC-B2C-fail");
        when(txRepo.findByOriginatorConversationId("ZC-B2C-fail")).thenReturn(Optional.of(storedTx));

        PayHeroWithdrawCallback callback = new PayHeroWithdrawCallback(
                "FAILED", "ZC-B2C-fail", BigDecimal.valueOf(50), "0700000001", null);
        service.handleWithdrawCallback(callback);

        ArgumentCaptor<PayoutFailed> captor = ArgumentCaptor.forClass(PayoutFailed.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().pickupId()).isEqualTo(pickupId);
    }

    @Test
    void handleStkCallback_success_publishesOrderPaid() {
        UUID orderId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        MpesaTransaction storedTx = buildStkTx(txId, orderId, "ws_CO_abc");
        when(txRepo.findByConversationId("ws_CO_abc")).thenReturn(Optional.of(storedTx));

        PayHeroStkCallback.StkCallbackResponse response =
                new PayHeroStkCallback.StkCallbackResponse("0", "Success", "ws_CO_abc", "OAR0001", BigDecimal.valueOf(100));
        PayHeroStkCallback callback = new PayHeroStkCallback("SUCCESS", "ZC-ORDER", response);

        service.handleStkCallback(callback);

        verify(eventPublisher).publishEvent(any(com.newton.zaocycle.payment.application.event.OrderPaid.class));
    }

    @Test
    void handleStkCallback_failure_publishesOrderPaymentFailed() {
        UUID orderId = UUID.randomUUID();
        MpesaTransaction storedTx = buildStkTx(UUID.randomUUID(), orderId, "ws_CO_xyz");
        when(txRepo.findByConversationId("ws_CO_xyz")).thenReturn(Optional.of(storedTx));

        PayHeroStkCallback.StkCallbackResponse response =
                new PayHeroStkCallback.StkCallbackResponse("1032", "Cancelled", "ws_CO_xyz", null, null);
        PayHeroStkCallback callback = new PayHeroStkCallback("FAILED", "ZC-ORDER", response);

        service.handleStkCallback(callback);

        verify(eventPublisher).publishEvent(any(OrderPaymentFailed.class));
    }

    private MpesaTransaction buildProcessingTx(UUID id, UUID pickupId, String originatorId) {
        return new MpesaTransaction(id, TransactionType.B2C, BigDecimal.valueOf(50),
                "4240", "+254700000001", originatorId,
                originatorId, null, TransactionStatus.PROCESSING,
                null, null, null, pickupId, null,
                Instant.now(), Instant.now());
    }

    private MpesaTransaction buildStkTx(UUID id, UUID orderId, String checkoutId) {
        return new MpesaTransaction(id, TransactionType.STK_PUSH, BigDecimal.valueOf(100),
                "+254700000001", "4240", "ZC-STK-orig",
                checkoutId, null, TransactionStatus.PROCESSING,
                null, null, null, null, orderId,
                Instant.now(), Instant.now());
    }
}

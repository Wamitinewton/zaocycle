package com.newton.zaocycle.payment.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newton.zaocycle.payment.application.event.PayoutCompleted;
import com.newton.zaocycle.payment.application.event.PayoutFailed;
import com.newton.zaocycle.payment.domain.model.MpesaTransaction;
import com.newton.zaocycle.payment.domain.model.TransactionStatus;
import com.newton.zaocycle.payment.domain.port.DarajaClient;
import com.newton.zaocycle.payment.domain.port.MpesaTransactionRepository;
import com.newton.zaocycle.payment.infrastructure.daraja.DarajaProperties;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.B2CRequest;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.B2CResponse;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.B2CResultCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MpesaServiceImplTest {

    @Mock private MpesaTransactionRepository txRepo;
    @Mock private DarajaClient darajaClient;
    @Mock private ApplicationEventPublisher eventPublisher;

    private MpesaServiceImpl service;
    private DarajaProperties props;

    @BeforeEach
    void setUp() {
        props = new DarajaProperties(
                "https://sandbox.safaricom.co.ke", "key", "secret",
                "600000", "testapi", "credential",
                "https://example.com/b2c/result",
                "https://example.com/b2c/timeout",
                "passkey",
                "https://example.com/stkpush/callback");
        lenient().when(txRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        service = new MpesaServiceImpl(txRepo, darajaClient, props, eventPublisher, new ObjectMapper());
    }

    @Test
    void initiateB2C_savesTwice_markProcessingOnSuccess() {
        UUID pickupId = UUID.randomUUID();
        B2CResponse response = new B2CResponse("convId", "origId", "OK");
        when(darajaClient.sendB2C(any(B2CRequest.class))).thenReturn(response);

        MpesaTransaction tx = service.initiateB2C(pickupId, "+254700000001", BigDecimal.valueOf(50));

        verify(txRepo, times(2)).save(any());
        assertThat(tx.status()).isEqualTo(TransactionStatus.PROCESSING);
        assertThat(tx.relatedPickupId()).isEqualTo(pickupId);
    }

    @Test
    void initiateB2C_marksFailed_whenDarajaThrows() {
        when(darajaClient.sendB2C(any())).thenThrow(new RuntimeException("network error"));

        MpesaTransaction tx = service.initiateB2C(UUID.randomUUID(), "+254700000001", BigDecimal.TEN);

        assertThat(tx.status()).isEqualTo(TransactionStatus.FAILED);
        verify(txRepo, times(2)).save(any());
    }

    @Test
    void handleB2CResult_success_publishesPayoutCompleted() {
        UUID pickupId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        MpesaTransaction storedTx = buildProcessingTx(txId, pickupId);
        when(txRepo.findByOriginatorConversationId("orig-123")).thenReturn(Optional.of(storedTx));

        B2CResultCallback.KeyValue kv = new B2CResultCallback.KeyValue("TransactionReceipt", "OAR000");
        B2CResultCallback.ResultParameters params = new B2CResultCallback.ResultParameters(List.of(kv));
        B2CResultCallback.B2CResult result = new B2CResultCallback.B2CResult(
                0, 0, "Success", "orig-123", "conv-1", "txid-1", params);
        B2CResultCallback callback = new B2CResultCallback(result);

        service.handleB2CResult(callback);

        ArgumentCaptor<PayoutCompleted> captor = ArgumentCaptor.forClass(PayoutCompleted.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().pickupId()).isEqualTo(pickupId);
        assertThat(captor.getValue().mpesaTransactionId()).isEqualTo(txId);
    }

    @Test
    void handleB2CResult_failure_publishesPayoutFailed() {
        UUID pickupId = UUID.randomUUID();
        MpesaTransaction storedTx = buildProcessingTx(UUID.randomUUID(), pickupId);
        when(txRepo.findByOriginatorConversationId("orig-456")).thenReturn(Optional.of(storedTx));

        B2CResultCallback.B2CResult result = new B2CResultCallback.B2CResult(
                0, 17, "Insufficient funds", "orig-456", "conv-2", "txid-2", null);
        service.handleB2CResult(new B2CResultCallback(result));

        ArgumentCaptor<PayoutFailed> captor = ArgumentCaptor.forClass(PayoutFailed.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().pickupId()).isEqualTo(pickupId);
    }

    @Test
    void handleB2CResult_unknownOriginatorId_doesNotThrow() {
        when(txRepo.findByOriginatorConversationId(any())).thenReturn(Optional.empty());
        B2CResultCallback.B2CResult result = new B2CResultCallback.B2CResult(
                0, 0, "OK", "unknown", "conv", "tx", null);
        service.handleB2CResult(new B2CResultCallback(result));
        verify(eventPublisher, never()).publishEvent(any());
    }

    private MpesaTransaction buildProcessingTx(UUID id, UUID pickupId) {
        MpesaTransaction tx = new MpesaTransaction(
                id,
                com.newton.zaocycle.payment.domain.model.TransactionType.B2C,
                BigDecimal.valueOf(50),
                "600000", "+254700000001",
                "orig-123", "conv-1", null,
                TransactionStatus.PROCESSING, null, null, null,
                pickupId, null,
                java.time.Instant.now(), java.time.Instant.now());
        return tx;
    }
}

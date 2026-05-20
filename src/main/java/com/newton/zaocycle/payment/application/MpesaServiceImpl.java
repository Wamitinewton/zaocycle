package com.newton.zaocycle.payment.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newton.zaocycle.payment.application.event.OrderPaid;
import com.newton.zaocycle.payment.application.event.OrderPaymentFailed;
import com.newton.zaocycle.payment.application.event.PayoutCompleted;
import com.newton.zaocycle.payment.application.event.PayoutFailed;
import com.newton.zaocycle.payment.domain.model.MpesaTransaction;
import com.newton.zaocycle.payment.domain.model.TransactionType;
import com.newton.zaocycle.payment.domain.port.DarajaClient;
import com.newton.zaocycle.payment.domain.port.MpesaTransactionRepository;
import com.newton.zaocycle.payment.infrastructure.daraja.DarajaProperties;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.B2CRequest;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.B2CResultCallback;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.StkPushCallback;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.StkPushRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
class MpesaServiceImpl implements MpesaService {

    private static final Logger log = LoggerFactory.getLogger(MpesaServiceImpl.class);
    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final ZoneId NAIROBI = ZoneId.of("Africa/Nairobi");

    private final MpesaTransactionRepository txRepo;
    private final DarajaClient darajaClient;
    private final DarajaProperties props;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    MpesaServiceImpl(MpesaTransactionRepository txRepo,
                     DarajaClient darajaClient,
                     DarajaProperties props,
                     ApplicationEventPublisher eventPublisher,
                     ObjectMapper objectMapper) {
        this.txRepo = txRepo;
        this.darajaClient = darajaClient;
        this.props = props;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public MpesaTransaction initiateB2C(UUID pickupId, String phone, BigDecimal amount) {
        String originatorId = "ZC-B2C-" + UUID.randomUUID();
        MpesaTransaction tx = MpesaTransaction.initiate(
                TransactionType.B2C, amount, props.shortcode(), phone, originatorId);
        tx.setRelatedPickupId(pickupId);
        tx = txRepo.save(tx);

        try {
            B2CRequest request = new B2CRequest(
                    props.initiatorName(), props.securityCredential(),
                    "BusinessPayment", amount,
                    props.shortcode(), phone,
                    "ZaoCycle waste collection payout",
                    props.b2cTimeoutUrl(), props.b2cResultUrl(),
                    originatorId);
            var response = darajaClient.sendB2C(request);
            tx.markProcessing(response.conversationId());
        } catch (Exception e) {
            log.error("Daraja B2C call failed for pickup {}: {}", pickupId, e.getMessage());
            tx.markFailed(e.getMessage());
        }

        return txRepo.save(tx);
    }

    @Override
    @Transactional
    public MpesaTransaction initiateStkPush(UUID orderId, String phone, BigDecimal amount, String reference) {
        String timestamp = ZonedDateTime.now(NAIROBI).format(TIMESTAMP_FMT);
        String rawPassword = props.stkShortcode() + props.stkPasskey() + timestamp;
        String password = Base64.getEncoder().encodeToString(
                rawPassword.getBytes(StandardCharsets.UTF_8));

        String originatorId = "ZC-STK-" + UUID.randomUUID();
        MpesaTransaction tx = MpesaTransaction.initiate(
                TransactionType.STK_PUSH, amount, phone, props.shortcode(), originatorId);
        tx.setRelatedOrderId(orderId);
        tx = txRepo.save(tx);

        try {
            StkPushRequest request = new StkPushRequest(
                    props.stkShortcode(), password, timestamp,
                    "CustomerPayBillOnline", amount,
                    phone, props.stkShortcode(), phone,
                    props.stkCallbackUrl(), reference,
                    "ZaoCycle Order Payment");
            var response = darajaClient.stkPush(request);
            tx.markProcessing(response.checkoutRequestId());
        } catch (Exception e) {
            log.error("Daraja STK Push failed for order {}: {}", orderId, e.getMessage());
            tx.markFailed(e.getMessage());
        }

        return txRepo.save(tx);
    }

    @Override
    @Transactional
    public void handleB2CResult(B2CResultCallback callback) {
        if (callback.result() == null) {
            log.warn("Received B2C callback with null result body");
            return;
        }
        B2CResultCallback.B2CResult result = callback.result();
        var txOpt = txRepo.findByOriginatorConversationId(result.originatorConversationId());
        if (txOpt.isEmpty()) {
            log.warn("No transaction found for originator ID: {}", result.originatorConversationId());
            return;
        }

        MpesaTransaction tx = txOpt.get();
        if (result.resultCode() == 0) {
            String receipt = extractB2CReceipt(result);
            String raw = serialize(callback);
            tx.markSuccess(receipt, raw);
            txRepo.save(tx);
            eventPublisher.publishEvent(new PayoutCompleted(
                    tx.relatedPickupId(), tx.id(),
                    tx.amount(), tx.partyB(),
                    receipt, null));
        } else {
            tx.markFailed(result.resultDesc());
            txRepo.save(tx);
            eventPublisher.publishEvent(new PayoutFailed(tx.relatedPickupId(), result.resultDesc()));
        }
    }

    @Override
    @Transactional
    public void handleB2CTimeout(Map<String, Object> body) {
        log.warn("B2C timeout callback received: {}", body);
        Object originatorId = body.get("OriginatorConversationID");
        if (originatorId == null) return;
        txRepo.findByOriginatorConversationId(originatorId.toString()).ifPresent(tx -> {
            tx.markTimeout();
            txRepo.save(tx);
            eventPublisher.publishEvent(new PayoutFailed(tx.relatedPickupId(), "B2C timeout"));
        });
    }

    @Override
    @Transactional
    public void handleStkPushCallback(StkPushCallback callback) {
        if (callback.body() == null || callback.body().stkCallback() == null) {
            log.warn("Received STK callback with null body");
            return;
        }
        StkPushCallback.StkCallback stk = callback.body().stkCallback();
        var txOpt = txRepo.findByConversationId(stk.checkoutRequestId());
        if (txOpt.isEmpty()) {
            log.warn("No transaction found for checkout request ID: {}", stk.checkoutRequestId());
            return;
        }

        MpesaTransaction tx = txOpt.get();
        if (stk.resultCode() == 0) {
            String receipt = extractStkReceipt(stk);
            tx.markSuccess(receipt, serialize(callback));
            txRepo.save(tx);
            eventPublisher.publishEvent(new OrderPaid(
                    tx.relatedOrderId(), tx.id(), null,
                    tx.partyA(), tx.amount(), null, null));
        } else {
            tx.markFailed(stk.resultDesc());
            txRepo.save(tx);
            eventPublisher.publishEvent(new OrderPaymentFailed(tx.relatedOrderId(), stk.resultDesc()));
        }
    }

    private String extractB2CReceipt(B2CResultCallback.B2CResult result) {
        if (result.resultParameters() == null || result.resultParameters().resultParameter() == null) {
            return null;
        }
        return result.resultParameters().resultParameter().stream()
                .filter(kv -> "TransactionReceipt".equals(kv.key()))
                .map(kv -> kv.value() != null ? kv.value().toString() : null)
                .findFirst().orElse(null);
    }

    private String extractStkReceipt(StkPushCallback.StkCallback stk) {
        if (stk.callbackMetadata() == null || stk.callbackMetadata().item() == null) {
            return null;
        }
        return stk.callbackMetadata().item().stream()
                .filter(item -> "MpesaReceiptNumber".equals(item.name()))
                .map(item -> item.value() != null ? item.value().toString() : null)
                .findFirst().orElse(null);
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize callback: {}", e.getMessage());
            return "{}";
        }
    }
}

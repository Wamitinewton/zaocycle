package com.newton.zaocycle.payment.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newton.zaocycle.payment.application.event.OrderPaid;
import com.newton.zaocycle.payment.application.event.OrderPaymentFailed;
import com.newton.zaocycle.payment.application.event.PayoutCompleted;
import com.newton.zaocycle.payment.application.event.PayoutFailed;
import com.newton.zaocycle.payment.domain.model.MpesaTransaction;
import com.newton.zaocycle.payment.domain.model.TransactionType;
import com.newton.zaocycle.payment.domain.port.MpesaTransactionRepository;
import com.newton.zaocycle.payment.domain.port.PayHeroClient;
import com.newton.zaocycle.payment.infrastructure.payhero.PayHeroProperties;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroStkCallback;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroStkPushRequest;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroWithdrawCallback;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroWithdrawRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
class PayHeroServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PayHeroServiceImpl.class);

    private final MpesaTransactionRepository txRepo;
    private final PayHeroClient payHeroClient;
    private final PayHeroProperties props;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    PayHeroServiceImpl(MpesaTransactionRepository txRepo,
                       PayHeroClient payHeroClient,
                       PayHeroProperties props,
                       ApplicationEventPublisher eventPublisher,
                       ObjectMapper objectMapper) {
        this.txRepo = txRepo;
        this.payHeroClient = payHeroClient;
        this.props = props;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public MpesaTransaction initiateB2C(UUID pickupId, String phone, BigDecimal amount) {
        String originatorId = "ZC-B2C-" + UUID.randomUUID();
        MpesaTransaction tx = MpesaTransaction.initiate(
                TransactionType.B2C, amount, String.valueOf(props.channelId()), phone, originatorId);
        tx.setRelatedPickupId(pickupId);
        tx = txRepo.save(tx);

        try {
            PayHeroWithdrawRequest request = new PayHeroWithdrawRequest(
                    "Safaricom", phone, amount, props.channelId(),
                    "ZaoCycle waste collection payout",
                    "b2c-payment", originatorId,
                    props.withdrawCallbackUrl());
            var response = payHeroClient.withdraw(request);
            if (!response.success()) {
                log.warn("PayHero withdraw returned success=false for pickup {}", pickupId);
            }
            tx.markProcessing(originatorId);
        } catch (Exception e) {
            log.error("PayHero withdraw failed for pickup {}: {}", pickupId, e.getMessage());
            tx.markFailed(e.getMessage());
        }

        return txRepo.save(tx);
    }

    @Override
    @Transactional
    public MpesaTransaction initiateStkPush(UUID orderId, String phone, BigDecimal amount, String reference) {
        String originatorId = "ZC-STK-" + UUID.randomUUID();
        MpesaTransaction tx = MpesaTransaction.initiate(
                TransactionType.STK_PUSH, amount, phone, String.valueOf(props.channelId()), originatorId);
        tx.setRelatedOrderId(orderId);
        tx = txRepo.save(tx);

        try {
            PayHeroStkPushRequest request = new PayHeroStkPushRequest(
                    amount, phone, props.channelId(),
                    "m-pesa", reference,
                    props.stkCallbackUrl());
            var response = payHeroClient.stkPush(request);
            if (response.checkoutRequestId() != null) {
                tx.markProcessing(response.checkoutRequestId());
            } else {
                log.warn("PayHero STK push returned no CheckoutRequestID for order {}", orderId);
            }
        } catch (Exception e) {
            log.error("PayHero STK push failed for order {}: {}", orderId, e.getMessage());
            tx.markFailed(e.getMessage());
        }

        return txRepo.save(tx);
    }

    @Override
    @Transactional
    public void handleStkCallback(PayHeroStkCallback callback) {
        if (callback.response() == null || callback.response().checkoutRequestId() == null) {
            log.warn("PayHero STK callback missing checkout request ID: {}", callback.externalReference());
            return;
        }

        String checkoutId = callback.response().checkoutRequestId();
        var txOpt = txRepo.findByConversationId(checkoutId);
        if (txOpt.isEmpty()) {
            log.warn("No transaction found for STK CheckoutRequestID: {}", checkoutId);
            return;
        }

        MpesaTransaction tx = txOpt.get();
        if ("SUCCESS".equalsIgnoreCase(callback.status())) {
            String receipt = callback.response().mpesaReceiptNumber();
            tx.markSuccess(receipt, serialize(callback));
            txRepo.save(tx);
            eventPublisher.publishEvent(new OrderPaid(
                    tx.relatedOrderId(), tx.id(), null,
                    tx.partyA(), tx.amount(), null, null));
        } else {
            String desc = callback.response().resultDesc() != null
                    ? callback.response().resultDesc() : callback.status();
            tx.markFailed(desc);
            txRepo.save(tx);
            eventPublisher.publishEvent(new OrderPaymentFailed(tx.relatedOrderId(), desc));
        }
    }

    @Override
    @Transactional
    public void handleWithdrawCallback(PayHeroWithdrawCallback callback) {
        if (callback.externalReference() == null) {
            log.warn("PayHero withdraw callback missing external_reference");
            return;
        }

        var txOpt = txRepo.findByOriginatorConversationId(callback.externalReference());
        if (txOpt.isEmpty()) {
            log.warn("No transaction found for withdraw external_reference: {}", callback.externalReference());
            return;
        }

        MpesaTransaction tx = txOpt.get();
        if ("SUCCESS".equalsIgnoreCase(callback.status())) {
            tx.markSuccess(callback.transactionId(), serialize(callback));
            txRepo.save(tx);
            eventPublisher.publishEvent(new PayoutCompleted(
                    tx.relatedPickupId(), tx.id(),
                    tx.amount(), tx.partyB(),
                    callback.transactionId(), null));
        } else {
            String desc = "Withdraw " + callback.status() + " for " + callback.externalReference();
            tx.markFailed(desc);
            txRepo.save(tx);
            eventPublisher.publishEvent(new PayoutFailed(tx.relatedPickupId(), desc));
        }
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

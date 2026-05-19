package com.newton.zaocycle.payment.domain.model;

import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class MpesaTransaction {

    private final UUID id;
    private final TransactionType transactionType;
    private final BigDecimal amount;
    private final String partyA;
    private final String partyB;
    private final String originatorConversationId;
    private String conversationId;
    private String mpesaReceiptNumber;
    private TransactionStatus status;
    private Integer resultCode;
    private String resultDesc;
    private String rawCallback;
    private UUID relatedPickupId;
    private UUID relatedOrderId;
    private final Instant createdAt;
    private Instant updatedAt;

    public MpesaTransaction(UUID id, TransactionType transactionType, BigDecimal amount,
                             String partyA, String partyB, String originatorConversationId,
                             String conversationId, String mpesaReceiptNumber,
                             TransactionStatus status, Integer resultCode, String resultDesc,
                             String rawCallback, UUID relatedPickupId, UUID relatedOrderId,
                             Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.partyA = partyA;
        this.partyB = partyB;
        this.originatorConversationId = originatorConversationId;
        this.conversationId = conversationId;
        this.mpesaReceiptNumber = mpesaReceiptNumber;
        this.status = status;
        this.resultCode = resultCode;
        this.resultDesc = resultDesc;
        this.rawCallback = rawCallback;
        this.relatedPickupId = relatedPickupId;
        this.relatedOrderId = relatedOrderId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MpesaTransaction initiate(TransactionType type, BigDecimal amount,
                                             String partyA, String partyB,
                                             String originatorConversationId) {
        Instant now = Instant.now();
        return new MpesaTransaction(IdGenerator.generate(), type, amount, partyA, partyB,
                originatorConversationId, null, null,
                TransactionStatus.INITIATED, null, null, null, null, null, now, now);
    }

    public void setRelatedPickupId(UUID id) {
        this.relatedPickupId = id;
        this.updatedAt = Instant.now();
    }

    public void setRelatedOrderId(UUID id) {
        this.relatedOrderId = id;
        this.updatedAt = Instant.now();
    }

    public void markProcessing(String conversationId) {
        this.conversationId = conversationId;
        this.status = TransactionStatus.PROCESSING;
        this.updatedAt = Instant.now();
    }

    public void markSuccess(String receiptNumber, String rawCallback) {
        this.mpesaReceiptNumber = receiptNumber;
        this.rawCallback = rawCallback;
        this.status = TransactionStatus.SUCCESS;
        this.updatedAt = Instant.now();
    }

    public void markFailed(String resultDesc) {
        this.resultDesc = resultDesc;
        this.status = TransactionStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public void markTimeout() {
        this.status = TransactionStatus.TIMEOUT;
        this.updatedAt = Instant.now();
    }

    public UUID id()                          { return id; }
    public TransactionType transactionType()  { return transactionType; }
    public BigDecimal amount()                { return amount; }
    public String partyA()                    { return partyA; }
    public String partyB()                    { return partyB; }
    public String originatorConversationId()  { return originatorConversationId; }
    public String conversationId()            { return conversationId; }
    public String mpesaReceiptNumber()        { return mpesaReceiptNumber; }
    public TransactionStatus status()         { return status; }
    public Integer resultCode()               { return resultCode; }
    public String resultDesc()                { return resultDesc; }
    public String rawCallback()               { return rawCallback; }
    public UUID relatedPickupId()             { return relatedPickupId; }
    public UUID relatedOrderId()              { return relatedOrderId; }
    public Instant createdAt()                { return createdAt; }
    public Instant updatedAt()                { return updatedAt; }
}

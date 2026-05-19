package com.newton.zaocycle.payment.infrastructure.persistence;

import com.newton.zaocycle.payment.domain.model.MpesaTransaction;
import com.newton.zaocycle.payment.domain.model.TransactionStatus;
import com.newton.zaocycle.payment.domain.model.TransactionType;

final class MpesaTransactionEntityMapper {

    private MpesaTransactionEntityMapper() {}

    static MpesaTransaction toDomain(MpesaTransactionEntity e) {
        return new MpesaTransaction(
                e.getId(),
                TransactionType.valueOf(e.getTransactionType()),
                e.getAmount(),
                e.getPartyA(), e.getPartyB(),
                e.getOriginatorConversationId(),
                e.getConversationId(),
                e.getMpesaReceiptNumber(),
                TransactionStatus.valueOf(e.getStatus()),
                e.getResultCode(), e.getResultDesc(),
                e.getRawCallback(),
                e.getRelatedPickupId(), e.getRelatedOrderId(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }

    static MpesaTransactionEntity toEntity(MpesaTransaction domain) {
        MpesaTransactionEntity e = new MpesaTransactionEntity();
        e.setId(domain.id());
        e.setTransactionType(domain.transactionType().name());
        e.setAmount(domain.amount());
        e.setPartyA(domain.partyA());
        e.setPartyB(domain.partyB());
        e.setOriginatorConversationId(domain.originatorConversationId());
        e.setConversationId(domain.conversationId());
        e.setMpesaReceiptNumber(domain.mpesaReceiptNumber());
        e.setStatus(domain.status().name());
        e.setResultCode(domain.resultCode());
        e.setResultDesc(domain.resultDesc());
        e.setRawCallback(domain.rawCallback());
        e.setRelatedPickupId(domain.relatedPickupId());
        e.setRelatedOrderId(domain.relatedOrderId());
        return e;
    }
}

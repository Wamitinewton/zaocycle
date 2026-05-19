package com.newton.zaocycle.payment.domain.port;

import com.newton.zaocycle.payment.domain.model.MpesaTransaction;

import java.util.Optional;
import java.util.UUID;

public interface MpesaTransactionRepository {
    Optional<MpesaTransaction> findById(UUID id);
    Optional<MpesaTransaction> findByOriginatorConversationId(String originatorId);
    Optional<MpesaTransaction> findByConversationId(String conversationId);
    MpesaTransaction save(MpesaTransaction tx);
}

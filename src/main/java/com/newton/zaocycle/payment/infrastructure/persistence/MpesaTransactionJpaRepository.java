package com.newton.zaocycle.payment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface MpesaTransactionJpaRepository extends JpaRepository<MpesaTransactionEntity, UUID> {
    Optional<MpesaTransactionEntity> findByOriginatorConversationId(String originatorId);

    Optional<MpesaTransactionEntity> findByConversationId(String conversationId);
}

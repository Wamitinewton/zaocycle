package com.newton.zaocycle.payment.infrastructure.persistence;

import com.newton.zaocycle.payment.domain.model.MpesaTransaction;
import com.newton.zaocycle.payment.domain.port.MpesaTransactionRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class MpesaTransactionRepositoryAdapter implements MpesaTransactionRepository {

    private final MpesaTransactionJpaRepository jpa;

    MpesaTransactionRepositoryAdapter(MpesaTransactionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<MpesaTransaction> findById(UUID id) {
        return jpa.findById(id).map(MpesaTransactionEntityMapper::toDomain);
    }

    @Override
    public Optional<MpesaTransaction> findByOriginatorConversationId(String originatorId) {
        return jpa.findByOriginatorConversationId(originatorId)
                .map(MpesaTransactionEntityMapper::toDomain);
    }

    @Override
    public Optional<MpesaTransaction> findByConversationId(String conversationId) {
        return jpa.findByConversationId(conversationId)
                .map(MpesaTransactionEntityMapper::toDomain);
    }

    @Override
    public MpesaTransaction save(MpesaTransaction tx) {
        return MpesaTransactionEntityMapper.toDomain(
                jpa.save(MpesaTransactionEntityMapper.toEntity(tx)));
    }
}

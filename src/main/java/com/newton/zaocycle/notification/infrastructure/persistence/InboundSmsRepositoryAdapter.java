package com.newton.zaocycle.notification.infrastructure.persistence;

import com.newton.zaocycle.notification.domain.model.InboundSms;
import com.newton.zaocycle.notification.domain.port.InboundSmsRepository;
import org.springframework.stereotype.Component;

@Component
class InboundSmsRepositoryAdapter implements InboundSmsRepository {

    private final InboundSmsJpaRepository jpa;

    InboundSmsRepositoryAdapter(InboundSmsJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public InboundSms save(InboundSms sms) {
        return InboundSmsEntityMapper.toDomain(jpa.save(InboundSmsEntityMapper.toEntity(sms)));
    }
}

package com.newton.zaocycle.notification.infrastructure.persistence;

import com.newton.zaocycle.notification.domain.model.OutboundSms;
import com.newton.zaocycle.notification.domain.port.OutboundSmsRepository;
import org.springframework.stereotype.Component;

@Component
class OutboundSmsRepositoryAdapter implements OutboundSmsRepository {

    private final OutboundSmsJpaRepository jpa;

    OutboundSmsRepositoryAdapter(OutboundSmsJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public OutboundSms save(OutboundSms sms) {
        return OutboundSmsEntityMapper.toDomain(jpa.save(OutboundSmsEntityMapper.toEntity(sms)));
    }
}

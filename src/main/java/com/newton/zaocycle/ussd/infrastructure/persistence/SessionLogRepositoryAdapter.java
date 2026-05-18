package com.newton.zaocycle.ussd.infrastructure.persistence;

import com.newton.zaocycle.ussd.domain.model.SessionLog;
import com.newton.zaocycle.ussd.domain.port.SessionLogRepository;
import org.springframework.stereotype.Component;

@Component
class SessionLogRepositoryAdapter implements SessionLogRepository {

    private final SessionLogJpaRepository jpa;

    SessionLogRepositoryAdapter(SessionLogJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(SessionLog log) {
        jpa.save(SessionLogEntity.builder()
                .id(log.id())
                .sessionId(log.sessionId())
                .phone(log.phone())
                .serviceCode(log.serviceCode())
                .inputText(log.inputText())
                .responseText(log.responseText())
                .responseType(log.responseType())
                .durationMs(log.durationMs())
                .errorMessage(log.errorMessage())
                .createdAt(log.createdAt())
                .build());
    }
}

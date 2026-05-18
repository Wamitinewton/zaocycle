package com.newton.zaocycle.ussd.application;

import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;
import com.newton.zaocycle.ussd.domain.model.SessionLog;
import com.newton.zaocycle.ussd.domain.port.SessionLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class SessionLogService {

    private static final Logger log = LoggerFactory.getLogger(SessionLogService.class);

    private final SessionLogRepository repository;

    public SessionLogService(SessionLogRepository repository) {
        this.repository = repository;
    }

    @Async
    @Transactional
    public void record(String sessionId, String phone, String serviceCode,
                       String inputText, String responseText, String responseType,
                       Integer durationMs, String errorMessage) {
        try {
            repository.save(new SessionLog(
                    IdGenerator.generate(), sessionId, phone, serviceCode,
                    inputText, responseText, responseType, durationMs, errorMessage, Instant.now()
            ));
        } catch (Exception e) {
            log.error("Failed to persist session log for sessionId={}", sessionId, e);
        }
    }
}

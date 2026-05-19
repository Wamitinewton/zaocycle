package com.newton.zaocycle.notification.infrastructure.persistence;

import com.newton.zaocycle.notification.domain.model.SmsTemplate;
import com.newton.zaocycle.notification.domain.port.SmsTemplateRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class SmsTemplateRepositoryAdapter implements SmsTemplateRepository {

    private final SmsTemplateJpaRepository jpa;

    SmsTemplateRepositoryAdapter(SmsTemplateJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<SmsTemplate> findByCode(String code) {
        return jpa.findByCode(code).map(SmsTemplateEntityMapper::toDomain);
    }

    @Override
    public List<SmsTemplate> findAll() {
        return jpa.findAll().stream().map(SmsTemplateEntityMapper::toDomain).toList();
    }
}

package com.newton.zaocycle.notification.domain.port;

import com.newton.zaocycle.notification.domain.model.SmsTemplate;

import java.util.List;
import java.util.Optional;

public interface SmsTemplateRepository {
    Optional<SmsTemplate> findByCode(String code);
    List<SmsTemplate> findAll();
}

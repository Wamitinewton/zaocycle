package com.newton.zaocycle.notification.infrastructure.persistence;

import com.newton.zaocycle.notification.domain.model.SmsTemplate;

class SmsTemplateEntityMapper {

    private SmsTemplateEntityMapper() {}

    static SmsTemplate toDomain(SmsTemplateEntity e) {
        return new SmsTemplate(e.getId(), e.getCode(), e.getBody(), e.getLanguage(), e.getCreatedAt());
    }

    static SmsTemplateEntity toEntity(SmsTemplate t) {
        SmsTemplateEntity e = new SmsTemplateEntity();
        e.setId(t.id());
        e.setCode(t.code());
        e.setBody(t.body());
        e.setLanguage(t.language());
        e.setCreatedAt(t.createdAt());
        return e;
    }
}

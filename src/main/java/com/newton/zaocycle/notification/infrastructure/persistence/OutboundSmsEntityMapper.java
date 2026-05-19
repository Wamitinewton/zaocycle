package com.newton.zaocycle.notification.infrastructure.persistence;

import com.newton.zaocycle.notification.domain.model.OutboundSms;

class OutboundSmsEntityMapper {

    private OutboundSmsEntityMapper() {}

    static OutboundSms toDomain(OutboundSmsEntity e) {
        return new OutboundSms(e.getId(), e.getPhone(), e.getTemplateCode(), e.getBody(),
                e.getStatus(), e.getProviderMessageId(), e.getErrorMessage(),
                e.getSentAt(), e.getCreatedAt());
    }

    static OutboundSmsEntity toEntity(OutboundSms sms) {
        OutboundSmsEntity e = new OutboundSmsEntity();
        e.setId(sms.id());
        e.setPhone(sms.phone());
        e.setTemplateCode(sms.templateCode());
        e.setBody(sms.body());
        e.setStatus(sms.status());
        e.setProviderMessageId(sms.providerMessageId());
        e.setErrorMessage(sms.errorMessage());
        e.setSentAt(sms.sentAt());
        e.setCreatedAt(sms.createdAt());
        return e;
    }
}

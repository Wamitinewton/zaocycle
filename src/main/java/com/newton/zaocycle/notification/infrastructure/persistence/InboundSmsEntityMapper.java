package com.newton.zaocycle.notification.infrastructure.persistence;

import com.newton.zaocycle.notification.domain.model.InboundSms;

class InboundSmsEntityMapper {

    private InboundSmsEntityMapper() {}

    static InboundSms toDomain(InboundSmsEntity e) {
        return new InboundSms(e.getId(), e.getPhone(), e.getBody(), e.getCommandParsed(),
                e.getStatus(), e.getErrorMessage(), e.getReceivedAt());
    }

    static InboundSmsEntity toEntity(InboundSms sms) {
        InboundSmsEntity e = new InboundSmsEntity();
        e.setId(sms.id());
        e.setPhone(sms.phone());
        e.setBody(sms.body());
        e.setCommandParsed(sms.commandParsed());
        e.setStatus(sms.status());
        e.setErrorMessage(sms.errorMessage());
        e.setReceivedAt(sms.receivedAt());
        return e;
    }
}

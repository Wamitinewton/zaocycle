package com.newton.zaocycle.notification.domain.port;

import com.newton.zaocycle.notification.domain.model.InboundSms;

public interface InboundSmsRepository {
    InboundSms save(InboundSms sms);
}

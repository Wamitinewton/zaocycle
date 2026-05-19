package com.newton.zaocycle.notification.domain.port;

import com.newton.zaocycle.notification.domain.model.OutboundSms;

public interface OutboundSmsRepository {
    OutboundSms save(OutboundSms sms);
}

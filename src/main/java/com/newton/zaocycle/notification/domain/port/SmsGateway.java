package com.newton.zaocycle.notification.domain.port;

public interface SmsGateway {
    SmsDispatchResult send(String phone, String body);
}

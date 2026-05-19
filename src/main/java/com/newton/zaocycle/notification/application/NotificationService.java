package com.newton.zaocycle.notification.application;

import java.util.Map;

public interface NotificationService {
    void sendTemplated(String phone, String templateCode, Map<String, String> vars);
    void sendRaw(String phone, String body);
}

package com.newton.zaocycle.ussd.infrastructure.africastalking;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "africastalking")
public class AfricasTalkingProperties {
    private String apiKey;
    private String username;
    private String senderId;
    private String baseUrl;
    private String serviceCode;
}

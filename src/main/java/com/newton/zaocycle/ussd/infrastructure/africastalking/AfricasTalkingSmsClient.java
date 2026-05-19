package com.newton.zaocycle.ussd.infrastructure.africastalking;

import com.newton.zaocycle.shared.infrastructure.africastalking.AfricasTalkingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AfricasTalkingSmsClient {

    private static final Logger log = LoggerFactory.getLogger(AfricasTalkingSmsClient.class);

    private final AfricasTalkingProperties props;

    public AfricasTalkingSmsClient(AfricasTalkingProperties props) {
        this.props = props;
    }

    public void sendSms(String phoneNumber, String message) {
        log.info("[SMS-STUB] To={} From={} Message={}", phoneNumber, props.senderId(), message);
    }
}

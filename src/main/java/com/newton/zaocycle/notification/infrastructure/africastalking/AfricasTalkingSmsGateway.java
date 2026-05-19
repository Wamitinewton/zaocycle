package com.newton.zaocycle.notification.infrastructure.africastalking;

import com.newton.zaocycle.notification.domain.port.SmsDispatchResult;
import com.newton.zaocycle.notification.domain.port.SmsGateway;
import com.newton.zaocycle.notification.infrastructure.africastalking.dto.AtRecipientDto;
import com.newton.zaocycle.notification.infrastructure.africastalking.dto.AtSmsResponseDto;
import com.newton.zaocycle.shared.infrastructure.africastalking.AfricasTalkingProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class AfricasTalkingSmsGateway implements SmsGateway {

    private final RestClient client;
    private final AfricasTalkingProperties props;

    public AfricasTalkingSmsGateway(RestClient.Builder builder, AfricasTalkingProperties props) {
        this.props = props;
        this.client = builder
                .baseUrl(props.baseUrl())
                .build();
    }

    @Override
    public SmsDispatchResult send(String phone, String body) {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("username", props.username());
        form.add("to", phone);
        form.add("message", body);
        form.add("from", props.senderId());

        AtSmsResponseDto response = client.post()
                .uri("/version1/messaging")
                .header("apiKey", props.apiKey())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(AtSmsResponseDto.class);

        AtRecipientDto recipient = response.smsMessageData().recipients().get(0);
        boolean ok = recipient.statusCode() == 101 || recipient.statusCode() == 102;
        return new SmsDispatchResult(ok, recipient.messageId(), ok ? null : recipient.status());
    }
}

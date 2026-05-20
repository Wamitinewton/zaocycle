package com.newton.zaocycle.notification.infrastructure.africastalking;

import com.newton.zaocycle.notification.domain.port.SmsDispatchResult;
import com.newton.zaocycle.notification.domain.port.SmsGateway;
import com.newton.zaocycle.notification.infrastructure.africastalking.dto.AtRecipientDto;
import com.newton.zaocycle.notification.infrastructure.africastalking.dto.AtSmsResponseDto;
import com.newton.zaocycle.shared.infrastructure.africastalking.AfricasTalkingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class AfricasTalkingSmsGateway implements SmsGateway {

    private static final Logger log = LoggerFactory.getLogger(AfricasTalkingSmsGateway.class);

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
        log.info("AT SMS send: to={} username={} senderId={} baseUrl={} bodyLen={}",
                phone, props.username(), props.senderId(), props.baseUrl(), body.length());

        var form = new LinkedMultiValueMap<String, String>();
        form.add("username", props.username());
        form.add("to", phone);
        form.add("message", body);
        if (props.senderId() != null && !props.senderId().isBlank()) {
            form.add("from", props.senderId());
        }

        AtSmsResponseDto response;
        try {
            response = client.post()
                    .uri("/version1/messaging")
                    .header("apiKey", props.apiKey())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(AtSmsResponseDto.class);
        } catch (RestClientException ex) {
            log.error("AT SMS HTTP error: to={} error={}", phone, ex.getMessage(), ex);
            return new SmsDispatchResult(false, null, ex.getMessage());
        }

        if (response == null || response.smsMessageData() == null
                || response.smsMessageData().recipients() == null
                || response.smsMessageData().recipients().isEmpty()) {
            log.error("AT SMS empty/null response body: to={} response={}", phone, response);
            return new SmsDispatchResult(false, null, "empty response from AT");
        }

        AtRecipientDto recipient = response.smsMessageData().recipients().get(0);
        boolean ok = recipient.statusCode() == 101 || recipient.statusCode() == 102;
        log.info("AT SMS result: to={} statusCode={} status={} messageId={} ok={}",
                phone, recipient.statusCode(), recipient.status(), recipient.messageId(), ok);
        return new SmsDispatchResult(ok, recipient.messageId(), ok ? null : recipient.status());
    }
}

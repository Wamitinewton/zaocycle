package com.newton.zaocycle.payment.infrastructure.daraja;

import com.newton.zaocycle.payment.domain.port.DarajaClient;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.B2CRequest;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.B2CResponse;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.StkPushRequest;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.StkPushResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
class DarajaClientImpl implements DarajaClient {

    private final DarajaProperties props;
    private final DarajaTokenService tokenService;
    private final RestClient restClient;

    DarajaClientImpl(DarajaProperties props,
                     DarajaTokenService tokenService,
                     RestClient.Builder restClientBuilder) {
        this.props = props;
        this.tokenService = tokenService;
        this.restClient = restClientBuilder
                .baseUrl(props.baseUrl())
                .build();
    }

    @Override
    public B2CResponse sendB2C(B2CRequest request) {
        return restClient.post()
                .uri("/mpesa/b2c/v3/paymentrequest")
                .header("Authorization", "Bearer " + tokenService.getToken())
                .body(request)
                .retrieve()
                .body(B2CResponse.class);
    }

    @Override
    public StkPushResponse stkPush(StkPushRequest request) {
        return restClient.post()
                .uri("/mpesa/stkpush/v1/processrequest")
                .header("Authorization", "Bearer " + tokenService.getToken())
                .body(request)
                .retrieve()
                .body(StkPushResponse.class);
    }

    @Override
    public String getToken() {
        return tokenService.getToken();
    }
}

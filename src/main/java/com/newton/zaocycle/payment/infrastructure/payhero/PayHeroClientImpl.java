package com.newton.zaocycle.payment.infrastructure.payhero;

import com.newton.zaocycle.payment.domain.port.PayHeroClient;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroStkPushRequest;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroStkPushResponse;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroWithdrawRequest;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroWithdrawResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
class PayHeroClientImpl implements PayHeroClient {

    private final PayHeroProperties props;
    private final RestClient restClient;

    PayHeroClientImpl(PayHeroProperties props, RestClient.Builder restClientBuilder) {
        this.props = props;
        this.restClient = restClientBuilder
                .baseUrl(props.baseUrl())
                .defaultHeader("Authorization", props.basicAuthToken())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    public PayHeroStkPushResponse stkPush(PayHeroStkPushRequest request) {
        return restClient.post()
                .uri("/api/v2/payments")
                .body(request)
                .retrieve()
                .body(PayHeroStkPushResponse.class);
    }

    @Override
    public PayHeroWithdrawResponse withdraw(PayHeroWithdrawRequest request) {
        return restClient.post()
                .uri("/api/v2/withdraw")
                .body(request)
                .retrieve()
                .body(PayHeroWithdrawResponse.class);
    }
}

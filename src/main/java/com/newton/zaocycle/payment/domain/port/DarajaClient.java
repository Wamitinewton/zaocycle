package com.newton.zaocycle.payment.domain.port;

import com.newton.zaocycle.payment.infrastructure.daraja.dto.B2CRequest;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.B2CResponse;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.StkPushRequest;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.StkPushResponse;

public interface DarajaClient {
    B2CResponse sendB2C(B2CRequest request);

    StkPushResponse stkPush(StkPushRequest request);

    String getToken();
}

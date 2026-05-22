package com.newton.zaocycle.payment.domain.port;

import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroStkPushRequest;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroStkPushResponse;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroWithdrawRequest;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroWithdrawResponse;

public interface PayHeroClient {
    PayHeroStkPushResponse stkPush(PayHeroStkPushRequest request);

    PayHeroWithdrawResponse withdraw(PayHeroWithdrawRequest request);
}

package com.newton.zaocycle.payment.api;

import com.newton.zaocycle.payment.application.PaymentService;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroStkCallback;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroWithdrawCallback;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments/payhero")
public class PayHeroCallbackController {

    private final PaymentService paymentService;

    public PayHeroCallbackController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/stk/callback")
    public Map<String, Object> stkCallback(@RequestBody PayHeroStkCallback callback) {
        paymentService.handleStkCallback(callback);
        return Map.of("ResultCode", 0, "ResultDesc", "Accepted");
    }

    @PostMapping("/withdraw/callback")
    public Map<String, Object> withdrawCallback(@RequestBody PayHeroWithdrawCallback callback) {
        paymentService.handleWithdrawCallback(callback);
        return Map.of("ResultCode", 0, "ResultDesc", "Accepted");
    }
}

package com.newton.zaocycle.payment.api;

import com.newton.zaocycle.payment.application.MpesaService;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.B2CResultCallback;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.StkPushCallback;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments/mpesa")
public class MpesaCallbackController {

    private final MpesaService mpesaService;

    public MpesaCallbackController(MpesaService mpesaService) {
        this.mpesaService = mpesaService;
    }

    @PostMapping("/b2c/result")
    public Map<String, Object> b2cResult(@RequestBody B2CResultCallback callback) {
        mpesaService.handleB2CResult(callback);
        return Map.of("ResponseCode", "0", "ResponseDesc", "Accepted");
    }

    @PostMapping("/b2c/timeout")
    public Map<String, Object> b2cTimeout(@RequestBody Map<String, Object> body) {
        mpesaService.handleB2CTimeout(body);
        return Map.of("ResponseCode", "0", "ResponseDesc", "Accepted");
    }

    @PostMapping("/stkpush/callback")
    public Map<String, Object> stkCallback(@RequestBody StkPushCallback callback) {
        mpesaService.handleStkPushCallback(callback);
        return Map.of("ResultCode", 0, "ResultDesc", "Accepted");
    }
}

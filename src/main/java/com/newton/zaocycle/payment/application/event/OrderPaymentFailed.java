package com.newton.zaocycle.payment.application.event;

import java.util.UUID;

public record OrderPaymentFailed(UUID orderId, String reason) {
}

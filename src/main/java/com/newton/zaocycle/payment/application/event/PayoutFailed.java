package com.newton.zaocycle.payment.application.event;

import java.util.UUID;

public record PayoutFailed(UUID pickupId, String reason) {}

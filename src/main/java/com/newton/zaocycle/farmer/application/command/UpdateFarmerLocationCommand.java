package com.newton.zaocycle.farmer.application.command;

import java.util.UUID;

public record UpdateFarmerLocationCommand(
        UUID farmerId,
        String tradingCenter,
        Double latitude,
        Double longitude
) {}

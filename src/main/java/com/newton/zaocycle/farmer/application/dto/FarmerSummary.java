package com.newton.zaocycle.farmer.application.dto;

import java.util.UUID;

public record FarmerSummary(UUID id, String phone, String fullName, boolean registrationComplete) {}

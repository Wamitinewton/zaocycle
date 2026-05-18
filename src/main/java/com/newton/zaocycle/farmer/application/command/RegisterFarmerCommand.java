package com.newton.zaocycle.farmer.application.command;

import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.domain.Ward;

public record RegisterFarmerCommand(PhoneNumber phone, String fullName, Ward ward, String pin) {}

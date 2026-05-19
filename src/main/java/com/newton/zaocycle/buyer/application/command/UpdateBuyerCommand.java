package com.newton.zaocycle.buyer.application.command;

public record UpdateBuyerCommand(String displayName, String contactPerson, String address, String ward) {}

package com.newton.zaocycle.rider.application.command;

public record RegisterRiderCommand(String phone, String fullName, String ward, String password) {}

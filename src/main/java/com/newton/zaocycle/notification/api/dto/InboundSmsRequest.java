package com.newton.zaocycle.notification.api.dto;

public record InboundSmsRequest(String from, String text, String date, String id) {}

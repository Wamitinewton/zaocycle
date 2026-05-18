package com.newton.zaocycle.ussd.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UssdCallbackRequest {
    private String sessionId;
    private String serviceCode;
    private String phoneNumber;
    private String text;
}

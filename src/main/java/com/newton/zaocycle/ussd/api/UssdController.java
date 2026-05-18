package com.newton.zaocycle.ussd.api;

import com.newton.zaocycle.ussd.api.dto.UssdCallbackRequest;
import com.newton.zaocycle.ussd.application.UssdGateway;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ussd")
public class UssdController {

    private final UssdGateway gateway;

    public UssdController(UssdGateway gateway) {
        this.gateway = gateway;
    }

    @PostMapping(
            path = "/callback",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String callback(@ModelAttribute UssdCallbackRequest request) {
        return gateway.handle(request);
    }
}

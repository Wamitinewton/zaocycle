package com.newton.zaocycle.notification.api;

import com.newton.zaocycle.notification.api.dto.InboundSmsRequest;
import com.newton.zaocycle.notification.application.InboundSmsDispatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sms")
public class InboundSmsController {

    private final InboundSmsDispatcher dispatcher;

    public InboundSmsController(InboundSmsDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @PostMapping("/inbound")
    public ResponseEntity<Void> inbound(@ModelAttribute InboundSmsRequest req) {
        dispatcher.handle(req.from(), req.text());
        return ResponseEntity.ok().build();
    }
}

package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.stereotype.Component;

class StubHandlers {

    @Component
    static class TerminatedHandler implements MenuHandler {
        @Override
        public MenuState state() {
            return MenuState.TERMINATED;
        }

        @Override
        public MenuResponse handle(UssdSession session, String input) {
            return ResponseBuilder.end("Session ended. Please dial again.");
        }
    }
}

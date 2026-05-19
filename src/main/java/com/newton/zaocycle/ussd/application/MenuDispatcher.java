package com.newton.zaocycle.ussd.application;

import com.newton.zaocycle.ussd.application.handler.MenuHandler;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MenuDispatcher {

    private static final Logger log = LoggerFactory.getLogger(MenuDispatcher.class);

    private final Map<MenuState, MenuHandler> handlers;

    public MenuDispatcher(List<MenuHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toUnmodifiableMap(MenuHandler::state, Function.identity()));
        log.info("Registered USSD handlers: {}", this.handlers.keySet());
    }

    public MenuResponse dispatch(UssdSession session, String input) {
        MenuHandler handler = handlers.get(session.getState());
        if (handler == null) {
            log.warn("No handler for state={} sessionId={}", session.getState(), session.getSessionId());
            return ResponseBuilder.end("Session error. Please dial again.");
        }
        log.debug("Handler {} processing input=[{}] sessionId={}",
                handler.getClass().getSimpleName(), input, session.getSessionId());
        MenuResponse response = handler.handle(session, input);
        log.debug("Handler {} -> newState={} responseType={}",
                handler.getClass().getSimpleName(), session.getState(),
                response instanceof MenuResponse.Continue ? "CON" : "END");
        return response;
    }
}

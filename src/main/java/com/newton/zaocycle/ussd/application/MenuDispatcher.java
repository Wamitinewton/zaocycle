package com.newton.zaocycle.ussd.application;

import com.newton.zaocycle.ussd.application.handler.MenuHandler;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MenuDispatcher {

    private final Map<MenuState, MenuHandler> handlers;

    public MenuDispatcher(List<MenuHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toUnmodifiableMap(MenuHandler::state, Function.identity()));
    }

    public MenuResponse dispatch(UssdSession session, String input) {
        MenuHandler handler = handlers.get(session.getState());
        if (handler == null) {
            return ResponseBuilder.end("Session error. Please dial again.");
        }
        return handler.handle(session, input);
    }
}

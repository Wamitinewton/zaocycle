package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.ussd.application.MenuDispatcher;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class MainMenuHandler implements MenuHandler {

    private final MenuDispatcher menuDispatcher;

    public MainMenuHandler(@Lazy MenuDispatcher menuDispatcher) {
        this.menuDispatcher = menuDispatcher;
    }

    @Override
    public MenuState state() {
        return MenuState.MAIN_MENU;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        return switch (input) {
            case "1" -> {
                session.setState(MenuState.LOG_SPRAY_CROP);
                yield menuDispatcher.dispatch(session, "");
            }
            case "2" -> {
                session.setState(MenuState.CHECK_HARVEST);
                yield menuDispatcher.dispatch(session, "");
            }
            case "3" -> {
                session.setState(MenuState.SCHEDULE_PICKUP_SELECT);
                yield menuDispatcher.dispatch(session, "");
            }
            case "4" -> {
                session.setState(MenuState.CHECK_EARNINGS);
                yield menuDispatcher.dispatch(session, "");
            }
            case "0" -> {
                session.setState(MenuState.TERMINATED);
                yield ResponseBuilder.end("Asante. Goodbye.");
            }
            default -> ResponseBuilder.cont(
                    """
                    Invalid choice.
                    1. Log pesticide spray
                    2. Check safe harvest date
                    3. Schedule waste pickup
                    4. My earnings
                    0. Exit""");
        };
    }
}

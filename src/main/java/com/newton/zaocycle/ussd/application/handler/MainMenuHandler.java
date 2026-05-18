package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.stereotype.Component;

@Component
public class MainMenuHandler implements MenuHandler {

    private final CheckHarvestHandler checkHarvestHandler;
    private final SchedulePickupHandler schedulePickupHandler;
    private final CheckEarningsHandler checkEarningsHandler;

    public MainMenuHandler(CheckHarvestHandler checkHarvestHandler,
                           SchedulePickupHandler schedulePickupHandler,
                           CheckEarningsHandler checkEarningsHandler) {
        this.checkHarvestHandler = checkHarvestHandler;
        this.schedulePickupHandler = schedulePickupHandler;
        this.checkEarningsHandler = checkEarningsHandler;
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
                yield ResponseBuilder.cont(LogSprayCropHandler.cropMenuText());
            }
            case "2" -> {
                session.setState(MenuState.CHECK_HARVEST);
                yield checkHarvestHandler.handle(session, "");
            }
            case "3" -> {
                session.setState(MenuState.SCHEDULE_PICKUP);
                yield schedulePickupHandler.handle(session, "");
            }
            case "4" -> {
                session.setState(MenuState.CHECK_EARNINGS);
                yield checkEarningsHandler.handle(session, "");
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

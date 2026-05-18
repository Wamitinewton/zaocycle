package com.newton.zaocycle.ussd.application;

import com.newton.zaocycle.ussd.api.dto.UssdCallbackRequest;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.stereotype.Service;

@Service
public class UssdGateway {

    private final UssdSessionService sessionService;
    private final MenuDispatcher dispatcher;
    private final SessionLogService logService;

    public UssdGateway(UssdSessionService sessionService, MenuDispatcher dispatcher,
                       SessionLogService logService) {
        this.sessionService = sessionService;
        this.dispatcher = dispatcher;
        this.logService = logService;
    }

    public String handle(UssdCallbackRequest request) {
        long start = System.currentTimeMillis();
        String responseText = null;
        String responseType = null;
        String errorMessage = null;

        try {
            String lastInput = extractLastInput(request.getText());
            UssdSession session = sessionService.loadOrCreate(
                    request.getSessionId(), request.getPhoneNumber());

            MenuResponse response = dispatcher.dispatch(session, lastInput);
            sessionService.save(session);

            String formatted = format(response);
            responseType = formatted.startsWith("CON") ? "CON" : "END";
            responseText = formatted.substring(4);

            return formatted;

        } catch (Exception e) {
            errorMessage = e.getMessage();
            responseType = "ERROR";
            return "END An error occurred. Please try again.";

        } finally {
            int durationMs = (int) (System.currentTimeMillis() - start);
            logService.record(
                    request.getSessionId(), request.getPhoneNumber(), request.getServiceCode(),
                    request.getText(), responseText, responseType, durationMs, errorMessage);
        }
    }

    private String extractLastInput(String text) {
        if (text == null || text.isBlank()) return "";
        int lastStar = text.lastIndexOf('*');
        return (lastStar >= 0) ? text.substring(lastStar + 1) : text;
    }

    private String format(MenuResponse response) {
        if (response instanceof MenuResponse.Continue c) return "CON " + c.text();
        if (response instanceof MenuResponse.End e) return "END " + e.text();
        throw new IllegalStateException("Unhandled MenuResponse type: " + response.getClass());
    }
}

package com.newton.zaocycle.ussd.application;

import com.newton.zaocycle.ussd.api.dto.UssdCallbackRequest;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UssdGateway {

    private static final Logger log = LoggerFactory.getLogger(UssdGateway.class);

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
            log.info("USSD request sessionId={} phone={} serviceCode={} input=[{}]",
                    request.getSessionId(), request.getPhoneNumber(),
                    request.getServiceCode(), lastInput);

            UssdSession session = sessionService.loadOrCreate(
                    request.getSessionId(), request.getPhoneNumber());

            log.debug("Dispatching state={} input=[{}]", session.getState(), lastInput);
            MenuResponse response = dispatcher.dispatch(session, lastInput);
            sessionService.save(session);

            String formatted = format(response);
            responseType = formatted.startsWith("CON") ? "CON" : "END";
            responseText = formatted.substring(4);

            log.info("USSD response sessionId={} type={} durationMs={}",
                    request.getSessionId(), responseType, System.currentTimeMillis() - start);
            return formatted;

        } catch (Exception e) {
            log.error("USSD error sessionId={} phone={} state=[{}] input=[{}]",
                    request.getSessionId(), request.getPhoneNumber(),
                    request.getText(), e.getMessage(), e);
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

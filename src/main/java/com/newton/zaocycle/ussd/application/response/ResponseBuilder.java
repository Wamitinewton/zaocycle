package com.newton.zaocycle.ussd.application.response;

public final class ResponseBuilder {

    private ResponseBuilder() {
    }

    public static MenuResponse cont(String text) {
        return new MenuResponse.Continue(text);
    }

    public static MenuResponse end(String text) {
        return new MenuResponse.End(text);
    }

}

package com.newton.zaocycle.ussd.application.response;

public sealed interface MenuResponse permits MenuResponse.Continue, MenuResponse.End {

    String text();

    record Continue(String text) implements MenuResponse {
    }

    record End(String text) implements MenuResponse {
    }
}

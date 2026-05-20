package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.shared.domain.TradingCenter;
import com.newton.zaocycle.shared.domain.Ward;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterVillageHandlerTest {

    private RegisterVillageHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RegisterVillageHandler();
    }

    @Test
    void handle_validSelection_storesLocationAndTransitionsToPinState() {
        UssdSession session = sessionWithWard(Ward.MWEA);

        MenuResponse response = handler.handle(session, "1");

        assertThat(response).isInstanceOf(MenuResponse.Continue.class);
        assertThat(session.getState()).isEqualTo(MenuState.REGISTER_PIN);
        assertThat(session.getString("tradingCenter")).isEqualTo("Mwea Town");
        assertThat(session.getString("latitude")).isNotNull();
        assertThat(session.getString("longitude")).isNotNull();
        assertThat(Double.parseDouble(session.getString("latitude")))
                .isEqualTo(TradingCenter.MWEA_TOWN.latitude());
        assertThat(Double.parseDouble(session.getString("longitude")))
                .isEqualTo(TradingCenter.MWEA_TOWN.longitude());
    }

    @Test
    void handle_lastOptionInWard_resolvedCorrectly() {
        UssdSession session = sessionWithWard(Ward.GICHUGU);
        int lastIndex = TradingCenter.forWard(Ward.GICHUGU).size();

        MenuResponse response = handler.handle(session, String.valueOf(lastIndex));

        assertThat(response).isInstanceOf(MenuResponse.Continue.class);
        TradingCenter expected = TradingCenter.fromIndex(Ward.GICHUGU, lastIndex);
        assertThat(session.getString("tradingCenter")).isEqualTo(expected.displayName());
    }

    @Test
    void handle_invalidNumber_returnsContinueWithMenu() {
        UssdSession session = sessionWithWard(Ward.MWEA);

        MenuResponse response = handler.handle(session, "99");

        assertThat(response).isInstanceOf(MenuResponse.Continue.class);
        assertThat(session.getState()).isEqualTo(MenuState.REGISTER_VILLAGE);
        assertThat(session.getString("tradingCenter")).isNull();
    }

    @Test
    void handle_nonNumericInput_returnsContinueWithMenu() {
        UssdSession session = sessionWithWard(Ward.NDIA);

        MenuResponse response = handler.handle(session, "abc");

        assertThat(response).isInstanceOf(MenuResponse.Continue.class);
        assertThat(session.getState()).isEqualTo(MenuState.REGISTER_VILLAGE);
    }

    @Test
    void handle_nullInput_returnsContinueWithMenu() {
        UssdSession session = sessionWithWard(Ward.KIRINYAGA_CENTRAL);

        MenuResponse response = handler.handle(session, null);

        assertThat(response).isInstanceOf(MenuResponse.Continue.class);
        assertThat(session.getState()).isEqualTo(MenuState.REGISTER_VILLAGE);
    }

    @Test
    void handle_wardNotInSession_returnsEndResponse() {
        UssdSession session = UssdSession.start("sid-001", "+254700000001", MenuState.REGISTER_VILLAGE);

        MenuResponse response = handler.handle(session, "1");

        assertThat(response).isInstanceOf(MenuResponse.End.class);
    }

    @Test
    void handle_corruptWardInSession_returnsEndResponse() {
        UssdSession session = UssdSession.start("sid-002", "+254700000002", MenuState.REGISTER_VILLAGE);
        session.put("ward", "INVALID_WARD");

        MenuResponse response = handler.handle(session, "1");

        assertThat(response).isInstanceOf(MenuResponse.End.class);
    }

    @Test
    void state_returnsRegisterVillage() {
        assertThat(handler.state()).isEqualTo(MenuState.REGISTER_VILLAGE);
    }

    private UssdSession sessionWithWard(Ward ward) {
        UssdSession session = UssdSession.start("sid-test", "+254700000001", MenuState.REGISTER_VILLAGE);
        session.put("ward", ward.name());
        return session;
    }
}

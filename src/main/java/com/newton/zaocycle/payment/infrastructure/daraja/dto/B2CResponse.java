package com.newton.zaocycle.payment.infrastructure.daraja.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record B2CResponse(
        @JsonProperty("ConversationID")           String conversationId,
        @JsonProperty("OriginatorConversationID") String originatorConversationId,
        @JsonProperty("ResponseDescription")      String responseDescription
) {}

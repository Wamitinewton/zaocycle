package com.newton.zaocycle.payment.infrastructure.daraja.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record B2CResultCallback(
        @JsonProperty("Result") B2CResult result
) {
    public record B2CResult(
            @JsonProperty("ResultType")               int resultType,
            @JsonProperty("ResultCode")               int resultCode,
            @JsonProperty("ResultDesc")               String resultDesc,
            @JsonProperty("OriginatorConversationID") String originatorConversationId,
            @JsonProperty("ConversationID")           String conversationId,
            @JsonProperty("TransactionID")            String transactionId,
            @JsonProperty("ResultParameters")         ResultParameters resultParameters
    ) {}

    public record ResultParameters(
            @JsonProperty("ResultParameter") List<KeyValue> resultParameter
    ) {}

    public record KeyValue(
            @JsonProperty("Key")   String key,
            @JsonProperty("Value") Object value
    ) {}
}

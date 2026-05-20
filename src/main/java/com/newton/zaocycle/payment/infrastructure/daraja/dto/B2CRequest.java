package com.newton.zaocycle.payment.infrastructure.daraja.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record B2CRequest(
        @JsonProperty("InitiatorName") String initiatorName,
        @JsonProperty("SecurityCredential") String securityCredential,
        @JsonProperty("CommandID") String commandId,
        @JsonProperty("Amount") BigDecimal amount,
        @JsonProperty("PartyA") String partyA,
        @JsonProperty("PartyB") String partyB,
        @JsonProperty("Remarks") String remarks,
        @JsonProperty("QueueTimeOutURL") String queueTimeOutUrl,
        @JsonProperty("ResultURL") String resultUrl,
        @JsonProperty("OriginatorConversationID") String originatorConversationId
) {
}

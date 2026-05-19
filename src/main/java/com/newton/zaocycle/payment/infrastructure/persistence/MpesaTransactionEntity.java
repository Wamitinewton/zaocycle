package com.newton.zaocycle.payment.infrastructure.persistence;

import com.newton.zaocycle.shared.infrastructure.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "mpesa_transactions")
@Getter
@Setter
@NoArgsConstructor
class MpesaTransactionEntity extends AuditableEntity {

    @Id
    private UUID id;

    @Column(name = "transaction_type", nullable = false, length = 20)
    private String transactionType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "party_a", nullable = false, length = 15)
    private String partyA;

    @Column(name = "party_b", nullable = false, length = 15)
    private String partyB;

    @Column(name = "originator_conversation_id", unique = true, length = 100)
    private String originatorConversationId;

    @Column(name = "conversation_id", length = 100)
    private String conversationId;

    @Column(name = "mpesa_receipt_number", length = 50)
    private String mpesaReceiptNumber;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "result_code")
    private Integer resultCode;

    @Column(name = "result_desc")
    private String resultDesc;

    @Column(name = "raw_callback", columnDefinition = "jsonb")
    private String rawCallback;

    @Column(name = "related_pickup_id")
    private UUID relatedPickupId;

    @Column(name = "related_order_id")
    private UUID relatedOrderId;
}

package com.newton.zaocycle.notification.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sms_inbound_log")
@Getter
@Setter
@NoArgsConstructor
class InboundSmsEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "command_parsed", length = 50)
    private String commandParsed;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "received_at", nullable = false, updatable = false)
    private Instant receivedAt;

    @PrePersist
    protected void onCreate() {
        if (receivedAt == null) receivedAt = Instant.now();
    }
}

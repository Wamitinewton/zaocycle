package com.newton.zaocycle.ussd.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ussd_session_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionLogEntity {

    @Id
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(name = "service_code", length = 20)
    private String serviceCode;

    @Column(name = "input_text", columnDefinition = "TEXT")
    private String inputText;

    @Column(name = "response_text", columnDefinition = "TEXT")
    private String responseText;

    @Column(name = "response_type", length = 10)
    private String responseType;

    @Column(name = "duration_ms")
    private Integer durationMs;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}

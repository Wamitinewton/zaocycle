CREATE TABLE ussd_session_logs
(
    id            UUID PRIMARY KEY,
    session_id    VARCHAR(255) NOT NULL,
    phone         VARCHAR(15)  NOT NULL,
    service_code  VARCHAR(20),
    input_text    TEXT,
    response_text TEXT,
    response_type VARCHAR(10),
    duration_ms   INTEGER,
    error_message TEXT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_response_type
        CHECK (response_type IN ('CON', 'END', 'ERROR'))
);

CREATE INDEX idx_session_logs_session ON ussd_session_logs (session_id);
CREATE INDEX idx_session_logs_phone ON ussd_session_logs (phone);
CREATE INDEX idx_session_logs_created ON ussd_session_logs (created_at DESC);

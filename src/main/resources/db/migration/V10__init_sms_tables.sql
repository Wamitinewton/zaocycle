CREATE TABLE sms_templates
(
    id         UUID PRIMARY KEY,
    code       VARCHAR(50) NOT NULL UNIQUE,
    body       TEXT        NOT NULL,
    language   VARCHAR(5)  NOT NULL DEFAULT 'en',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE sms_outbound_log
(
    id                  UUID PRIMARY KEY,
    phone               VARCHAR(15) NOT NULL,
    template_code       VARCHAR(50),
    body                TEXT        NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    provider_message_id VARCHAR(100),
    error_message       TEXT,
    sent_at             TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_sms_status CHECK (status IN ('PENDING', 'SENT', 'FAILED', 'DELIVERED'))
);

CREATE TABLE sms_inbound_log
(
    id             UUID PRIMARY KEY,
    phone          VARCHAR(15) NOT NULL,
    body           TEXT        NOT NULL,
    command_parsed VARCHAR(50),
    status         VARCHAR(20) NOT NULL DEFAULT 'RECEIVED',
    error_message  TEXT,
    received_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_sms_outbound_phone ON sms_outbound_log (phone);
CREATE INDEX idx_sms_inbound_phone ON sms_inbound_log (phone);

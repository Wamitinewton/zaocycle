CREATE TABLE mpesa_transactions
(
    id                         UUID PRIMARY KEY,
    transaction_type           VARCHAR(20)    NOT NULL,
    amount                     NUMERIC(12, 2) NOT NULL,
    party_a                    VARCHAR(15)    NOT NULL,
    party_b                    VARCHAR(15)    NOT NULL,
    originator_conversation_id VARCHAR(100) UNIQUE,
    conversation_id            VARCHAR(100),
    mpesa_receipt_number       VARCHAR(50),
    status                     VARCHAR(20)    NOT NULL DEFAULT 'INITIATED',
    result_code                INTEGER,
    result_desc                TEXT,
    raw_callback               JSONB,
    related_pickup_id          UUID REFERENCES waste_pickups (id),
    related_order_id           UUID,
    created_at                 TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at                 TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT chk_mpesa_type CHECK (transaction_type IN ('B2C', 'STK_PUSH')),
    CONSTRAINT chk_mpesa_status CHECK (status IN ('INITIATED', 'PROCESSING', 'SUCCESS', 'FAILED', 'TIMEOUT'))
);

CREATE INDEX idx_mpesa_originator ON mpesa_transactions (originator_conversation_id);
CREATE INDEX idx_mpesa_status_created ON mpesa_transactions (status, created_at DESC);
CREATE INDEX idx_mpesa_pickup ON mpesa_transactions (related_pickup_id);
CREATE INDEX idx_mpesa_order ON mpesa_transactions (related_order_id);

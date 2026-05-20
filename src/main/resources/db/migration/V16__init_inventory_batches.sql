CREATE TABLE waste_intake_batches
(
    id          UUID PRIMARY KEY,
    intake_date DATE           NOT NULL,
    total_kg    NUMERIC(10, 2) NOT NULL,
    pickup_ids  JSONB          NOT NULL,
    notes       TEXT,
    recorded_by UUID REFERENCES staff_users (id),
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE TABLE briquette_batches
(
    id               UUID PRIMARY KEY,
    batch_number     VARCHAR(50)    NOT NULL UNIQUE,
    kg_produced      NUMERIC(10, 2) NOT NULL,
    kg_remaining     NUMERIC(10, 2) NOT NULL,
    produced_at      TIMESTAMPTZ    NOT NULL,
    source_intake_id UUID REFERENCES waste_intake_batches (id),
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE INDEX idx_briquette_remaining ON briquette_batches (kg_remaining) WHERE kg_remaining > 0;

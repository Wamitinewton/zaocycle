CREATE TABLE waste_pickups
(
    id                   UUID PRIMARY KEY,
    farmer_id            UUID        NOT NULL REFERENCES farmers (id),
    rider_id             UUID REFERENCES riders (id),
    application_id       UUID REFERENCES pesticide_applications (id),
    requested_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    scheduled_for        DATE        NOT NULL,
    status               VARCHAR(20) NOT NULL DEFAULT 'REQUESTED',
    weight_kg            NUMERIC(10, 2),
    photo_url            TEXT,
    notes                TEXT,
    payout_amount        NUMERIC(10, 2),
    mpesa_transaction_id UUID,
    collected_at         TIMESTAMPTZ,
    paid_at              TIMESTAMPTZ,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_pickup_status
        CHECK (status IN ('REQUESTED', 'ASSIGNED', 'COLLECTED', 'PAID', 'CANCELLED', 'FAILED'))
);

CREATE INDEX idx_pickups_farmer ON waste_pickups (farmer_id);
CREATE INDEX idx_pickups_rider_status ON waste_pickups (rider_id, status);
CREATE INDEX idx_pickups_status_scheduled ON waste_pickups (status, scheduled_for);
CREATE INDEX idx_pickups_farmer_paid ON waste_pickups (farmer_id, status) WHERE status = 'PAID';

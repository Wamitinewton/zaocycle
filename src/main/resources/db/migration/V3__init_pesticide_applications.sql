CREATE TABLE pesticide_applications (
    id               UUID PRIMARY KEY,
    farmer_id        UUID NOT NULL REFERENCES farmers(id),
    chemical_id      UUID NOT NULL REFERENCES chemicals(id),
    crop             VARCHAR(100) NOT NULL,
    quantity_ml      NUMERIC(10, 2),
    applied_at       TIMESTAMPTZ NOT NULL,
    safe_harvest_date DATE NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    source           VARCHAR(20) NOT NULL DEFAULT 'USSD',
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_application_status
        CHECK (status IN ('PENDING', 'SAFE', 'EXPIRED', 'INVALIDATED')),
    CONSTRAINT chk_application_source
        CHECK (source IN ('USSD', 'WEB', 'API'))
);

CREATE INDEX idx_applications_farmer ON pesticide_applications(farmer_id);
CREATE INDEX idx_applications_status ON pesticide_applications(status);
CREATE INDEX idx_applications_safe_date ON pesticide_applications(safe_harvest_date);

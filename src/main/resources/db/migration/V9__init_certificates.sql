CREATE TABLE certificates
(
    id               UUID PRIMARY KEY,
    application_id   UUID        NOT NULL UNIQUE REFERENCES pesticide_applications (id),
    token            VARCHAR(32) NOT NULL UNIQUE,
    qr_image_url     TEXT        NOT NULL,
    issued_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires_at       TIMESTAMPTZ NOT NULL,
    verified_count   INTEGER     NOT NULL DEFAULT 0,
    last_verified_at TIMESTAMPTZ,
    status           VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT chk_certificate_status CHECK (status IN ('ACTIVE', 'EXPIRED', 'REVOKED'))
);

CREATE INDEX idx_certificates_token ON certificates (token);
CREATE INDEX idx_certificates_status ON certificates (status);

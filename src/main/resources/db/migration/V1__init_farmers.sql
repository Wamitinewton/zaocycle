CREATE TABLE farmers
(
    id                    UUID PRIMARY KEY,
    phone                 VARCHAR(15) NOT NULL UNIQUE,
    full_name             VARCHAR(255),
    ward                  VARCHAR(50),
    pin_hash              VARCHAR(255),
    registration_complete BOOLEAN     NOT NULL DEFAULT false,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_farmers_phone ON farmers (phone);

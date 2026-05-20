CREATE TABLE riders
(
    id            UUID PRIMARY KEY,
    phone         VARCHAR(15)  NOT NULL UNIQUE,
    full_name     VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    assigned_ward VARCHAR(50)  NOT NULL,
    active        BOOLEAN      NOT NULL DEFAULT true,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_riders_phone ON riders (phone);
CREATE INDEX idx_riders_ward_active ON riders (assigned_ward, active);

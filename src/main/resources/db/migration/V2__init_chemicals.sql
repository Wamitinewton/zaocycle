CREATE TABLE chemicals (
    id                UUID PRIMARY KEY,
    name              VARCHAR(255) NOT NULL,
    active_ingredient VARCHAR(255),
    category          VARCHAR(30) NOT NULL,
    half_life_days    INTEGER NOT NULL,
    phi_days          INTEGER NOT NULL,
    common_crops      VARCHAR(500),
    active            BOOLEAN NOT NULL DEFAULT true,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_chemical_category
        CHECK (category IN ('FUNGICIDE', 'INSECTICIDE', 'HERBICIDE', 'OTHER'))
);

CREATE INDEX idx_chemicals_active ON chemicals(active);
CREATE INDEX idx_chemicals_category ON chemicals(category);

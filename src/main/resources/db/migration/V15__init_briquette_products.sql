CREATE TABLE briquette_products (
    id          UUID PRIMARY KEY,
    sku         VARCHAR(50)  NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    weight_kg   NUMERIC(8,2) NOT NULL,
    unit_price  NUMERIC(10,2) NOT NULL,
    image_url   TEXT,
    active      BOOLEAN      NOT NULL DEFAULT true,
    sort_order  INTEGER      NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_products_active ON briquette_products(active, sort_order);

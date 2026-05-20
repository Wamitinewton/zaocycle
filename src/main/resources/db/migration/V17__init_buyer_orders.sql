CREATE TABLE buyer_orders
(
    id                   UUID PRIMARY KEY,
    buyer_id             UUID           NOT NULL REFERENCES buyers (id),
    product_id           UUID           NOT NULL REFERENCES briquette_products (id),
    quantity             INTEGER        NOT NULL CHECK (quantity > 0),
    unit_price           NUMERIC(10, 2) NOT NULL,
    total_kg             NUMERIC(10, 2) NOT NULL,
    total_amount         NUMERIC(12, 2) NOT NULL,
    delivery_address     TEXT           NOT NULL,
    delivery_phone       VARCHAR(15)    NOT NULL,
    requested_delivery   DATE,
    delivered_at         TIMESTAMPTZ,
    status               VARCHAR(20)    NOT NULL DEFAULT 'PENDING_PAYMENT',
    mpesa_transaction_id UUID REFERENCES mpesa_transactions (id),
    notes                TEXT,
    created_at           TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT chk_order_status
        CHECK (status IN ('PENDING_PAYMENT', 'PAID', 'READY_FOR_DELIVERY', 'DELIVERED', 'CANCELLED', 'REFUNDED'))
);

CREATE INDEX idx_orders_buyer ON buyer_orders (buyer_id, created_at DESC);
CREATE INDEX idx_orders_status ON buyer_orders (status, created_at DESC);
CREATE INDEX idx_orders_mpesa_tx ON buyer_orders (mpesa_transaction_id);

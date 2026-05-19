CREATE TABLE buyers (
    id             UUID PRIMARY KEY,
    email          VARCHAR(255) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    phone          VARCHAR(15)  NOT NULL,
    buyer_type     VARCHAR(20)  NOT NULL,
    display_name   VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    address        TEXT,
    ward           VARCHAR(50),
    active         BOOLEAN      NOT NULL DEFAULT true,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_buyer_type CHECK (buyer_type IN ('SCHOOL','INDIVIDUAL','INSTITUTION','BUSINESS'))
);

CREATE INDEX idx_buyers_email ON buyers(email);
CREATE INDEX idx_buyers_phone ON buyers(phone);

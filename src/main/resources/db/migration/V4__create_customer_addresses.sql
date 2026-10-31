CREATE TABLE customer_addresses (
    id            UUID          PRIMARY KEY,
    customer_id   UUID          NOT NULL REFERENCES customers (id),
    label         VARCHAR(20)   NOT NULL,
    house_number  VARCHAR(50),
    street        VARCHAR(255),
    landmark      VARCHAR(255),
    city          VARCHAR(100)  NOT NULL,
    state         VARCHAR(100)  NOT NULL,
    postal_code   VARCHAR(10)   NOT NULL,
    latitude      NUMERIC(9,6),
    longitude     NUMERIC(9,6),
    is_default    BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ   NOT NULL,
    updated_at    TIMESTAMPTZ   NOT NULL,

    CONSTRAINT chk_customer_addresses_label CHECK (label IN ('HOME', 'WORK', 'OTHER'))
);

CREATE INDEX idx_customer_addresses_customer_id ON customer_addresses (customer_id);

-- Database-level guarantee of "only one default address per customer".
-- A partial index only covers rows where is_default is true, so any number of
-- non-default addresses is fine, but a second default for the same customer is rejected.
CREATE UNIQUE INDEX uq_customer_addresses_one_default
    ON customer_addresses (customer_id) WHERE is_default;

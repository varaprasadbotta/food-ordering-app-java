CREATE TABLE customers (
    id             UUID          PRIMARY KEY,
    first_name     VARCHAR(100)  NOT NULL,
    last_name      VARCHAR(100)  NOT NULL,
    email          VARCHAR(255)  NOT NULL,
    phone_number   VARCHAR(20)   NOT NULL,
    password_hash  VARCHAR(255)  NOT NULL,
    active         BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ   NOT NULL,
    updated_at     TIMESTAMPTZ   NOT NULL,

    CONSTRAINT uq_customers_phone_number UNIQUE (phone_number)
);

-- Case-insensitive: "Prasad@Example.com" and "prasad@example.com" are the same account.
CREATE UNIQUE INDEX uq_customers_email ON customers (LOWER(email));

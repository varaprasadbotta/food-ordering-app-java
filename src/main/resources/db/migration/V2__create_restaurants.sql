CREATE TABLE restaurants (
    id                     UUID          PRIMARY KEY,
    name                   VARCHAR(150)  NOT NULL,
    description            TEXT,
    phone_number           VARCHAR(20)   NOT NULL,
    email                  VARCHAR(255),
    average_rating         NUMERIC(2,1)  NOT NULL DEFAULT 0,
    total_ratings_count    INTEGER       NOT NULL DEFAULT 0,
    pure_veg               BOOLEAN       NOT NULL DEFAULT FALSE,
    active                 BOOLEAN       NOT NULL DEFAULT TRUE,
    accepting_orders       BOOLEAN       NOT NULL DEFAULT TRUE,
    commission_percentage  NUMERIC(5,2)  NOT NULL,
    created_at             TIMESTAMPTZ   NOT NULL,
    updated_at             TIMESTAMPTZ   NOT NULL,

    CONSTRAINT chk_restaurants_rating     CHECK (average_rating BETWEEN 0 AND 5),
    CONSTRAINT chk_restaurants_ratings    CHECK (total_ratings_count >= 0),
    CONSTRAINT chk_restaurants_commission CHECK (commission_percentage BETWEEN 0 AND 100)
);

-- A restaurant serves many cuisines; a list cannot live in one relational column.
-- position keeps the order the restaurant listed them in.
CREATE TABLE restaurant_cuisines (
    restaurant_id  UUID         NOT NULL REFERENCES restaurants (id) ON DELETE CASCADE,
    cuisine        VARCHAR(50)  NOT NULL,
    position       INTEGER      NOT NULL,

    PRIMARY KEY (restaurant_id, cuisine)
);

-- Supports "find all restaurants serving <cuisine>"
CREATE INDEX idx_restaurant_cuisines_cuisine ON restaurant_cuisines (cuisine);

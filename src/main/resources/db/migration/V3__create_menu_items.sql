CREATE TABLE menu_items (
    id             UUID           PRIMARY KEY,
    restaurant_id  UUID           NOT NULL REFERENCES restaurants (id),
    name           VARCHAR(150)   NOT NULL,
    description    TEXT,
    price          NUMERIC(10,2)  NOT NULL,
    category       VARCHAR(30)    NOT NULL,
    dietary_type   VARCHAR(20)    NOT NULL,
    image_url      TEXT,
    available      BOOLEAN        NOT NULL DEFAULT TRUE,
    recommended    BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ    NOT NULL,
    updated_at     TIMESTAMPTZ    NOT NULL,

    CONSTRAINT chk_menu_items_price        CHECK (price > 0),
    CONSTRAINT chk_menu_items_category     CHECK (category IN ('STARTER', 'MAIN_COURSE', 'DESSERT', 'BEVERAGE')),
    CONSTRAINT chk_menu_items_dietary_type CHECK (dietary_type IN ('VEG', 'NON_VEG', 'EGG'))
);

-- Foreign keys are not indexed automatically in PostgreSQL.
-- This one backs the most common query: "show this restaurant's menu".
CREATE INDEX idx_menu_items_restaurant_id ON menu_items (restaurant_id);

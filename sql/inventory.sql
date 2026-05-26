-- =========================
-- INVENTORY TABLE
-- =========================

CREATE TABLE IF NOT EXISTS inventory
(
    inventory_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    available_quantity INTEGER NOT NULL DEFAULT 0,
    reserved_quantity INTEGER NOT NULL DEFAULT 0,

    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255),
    updated_date TIMESTAMP,

    CONSTRAINT pk_inventory PRIMARY KEY (inventory_id),

    CONSTRAINT uk_inventory_product UNIQUE (product_id),

    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id)
        REFERENCES product(product_id),

    CONSTRAINT chk_inventory_available_quantity
        CHECK (available_quantity >= 0),

    CONSTRAINT chk_inventory_reserved_quantity
        CHECK (reserved_quantity >= 0)
);

CREATE SEQUENCE IF NOT EXISTS inventory_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE inventory
ALTER COLUMN inventory_id
SET DEFAULT nextval('inventory_seq');

ALTER SEQUENCE inventory_seq
OWNED BY inventory.inventory_id;




-- =========================
-- INVENTORY TRANSACTION TABLE
-- =========================

CREATE TABLE IF NOT EXISTS inventory_transaction
(
    transaction_id BIGINT NOT NULL,
    inventory_id BIGINT NOT NULL,

    transaction_type VARCHAR(50) NOT NULL,

    order_id BIGINT,
    order_number VARCHAR(100),

    available_before_quantity INTEGER NOT NULL,
    available_after_quantity INTEGER NOT NULL,

    reserved_before_quantity INTEGER NOT NULL,
    reserved_after_quantity INTEGER NOT NULL,

    quantity INTEGER NOT NULL,
    remarks VARCHAR(500),

    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255),
    updated_date TIMESTAMP,

    CONSTRAINT pk_inventory_transaction PRIMARY KEY (transaction_id),

    CONSTRAINT fk_inventory_transaction_inventory
        FOREIGN KEY (inventory_id)
        REFERENCES inventory(inventory_id),

    CONSTRAINT fk_inventory_transaction_order
        FOREIGN KEY (order_id)
        REFERENCES orders(order_id),

    CONSTRAINT chk_inventory_transaction_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_inventory_transaction_available_before
        CHECK (available_before_quantity >= 0),

    CONSTRAINT chk_inventory_transaction_available_after
        CHECK (available_after_quantity >= 0),

    CONSTRAINT chk_inventory_transaction_reserved_before
        CHECK (reserved_before_quantity >= 0),

    CONSTRAINT chk_inventory_transaction_reserved_after
        CHECK (reserved_after_quantity >= 0)
);

CREATE SEQUENCE IF NOT EXISTS inventory_transaction_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE inventory_transaction
ALTER COLUMN transaction_id
SET DEFAULT nextval('inventory_transaction_seq');

ALTER SEQUENCE inventory_transaction_seq
OWNED BY inventory_transaction.transaction_id;
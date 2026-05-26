CREATE TABLE IF NOT EXISTS orders
(
    order_id BIGINT NOT NULL,
    order_number VARCHAR(100) NOT NULL,
    order_status VARCHAR(50) NOT NULL,
    customer_name VARCHAR(255),
    total_amount NUMERIC(12, 2) NOT NULL DEFAULT 0,

    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255),
    updated_date TIMESTAMP,

    CONSTRAINT pk_orders PRIMARY KEY (order_id),
    CONSTRAINT uk_orders_order_number UNIQUE (order_number)
);

CREATE SEQUENCE IF NOT EXISTS orders_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE orders
ALTER COLUMN order_id
SET DEFAULT nextval('orders_seq');

ALTER SEQUENCE orders_seq
OWNED BY orders.order_id;



CREATE TABLE IF NOT EXISTS order_items
(
    order_item_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,

    product_name VARCHAR(255) NOT NULL,
    sku VARCHAR(255) NOT NULL,

    quantity INTEGER NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    total_price NUMERIC(12, 2) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255),
    updated_date TIMESTAMP,

    CONSTRAINT pk_order_items PRIMARY KEY (order_item_id),

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders(order_id),

    CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id)
        REFERENCES product(product_id),

    CONSTRAINT chk_order_items_quantity
        CHECK (quantity > 0)
);

CREATE SEQUENCE IF NOT EXISTS order_items_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE order_items
ALTER COLUMN order_item_id
SET DEFAULT nextval('order_items_seq');

ALTER SEQUENCE order_items_seq
OWNED BY order_items.order_item_id;





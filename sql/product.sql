
-- =========================
-- PRODUCT CATEGORY TABLE
-- =========================

CREATE TABLE IF NOT EXISTS product_category
(
    category_id BIGINT NOT NULL,
    category_name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255),
    updated_date TIMESTAMP,
    CONSTRAINT pk_product_category PRIMARY KEY (category_id),
    CONSTRAINT uk_product_category_name UNIQUE (category_name)
);

CREATE SEQUENCE IF NOT EXISTS product_category_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE product_category
ALTER COLUMN category_id
SET DEFAULT nextval('product_category_seq');

ALTER SEQUENCE product_category_seq
OWNED BY product_category.category_id;




-- =========================
-- PRODUCT TABLE
-- =========================

CREATE TABLE IF NOT EXISTS product
(
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    sku VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price NUMERIC(12, 2) NOT NULL,
    category_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255),
    updated_date TIMESTAMP,
    CONSTRAINT pk_product PRIMARY KEY (product_id),
    CONSTRAINT uk_product_sku UNIQUE (sku),
    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES product_category(category_id)
);

CREATE SEQUENCE IF NOT EXISTS product_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE product
ALTER COLUMN product_id
SET DEFAULT nextval('product_seq');

ALTER SEQUENCE product_seq
OWNED BY product.product_id;


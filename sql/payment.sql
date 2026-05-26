CREATE TABLE IF NOT EXISTS payments
(
    payment_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    payment_number VARCHAR(100) NOT NULL,
    customer_name VARCHAR(255),
    amount NUMERIC(12, 2) NOT NULL,
    payment_mode VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    remark VARCHAR(500),

    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255),
    updated_date TIMESTAMP,

    CONSTRAINT pk_payments PRIMARY KEY (payment_id),
    CONSTRAINT uk_payments_order UNIQUE (order_id),
    CONSTRAINT uk_payments_payment_number UNIQUE (payment_number),

    CONSTRAINT fk_payments_order
        FOREIGN KEY (order_id)
        REFERENCES orders(order_id)
);


CREATE SEQUENCE IF NOT EXISTS payments_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE payments
ALTER COLUMN payment_id
SET DEFAULT nextval('payments_seq');

ALTER SEQUENCE payments_seq
OWNED BY payments.payment_id;
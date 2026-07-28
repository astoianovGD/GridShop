CREATE TABLE roles
(
    role_id BIGSERIAL PRIMARY KEY,
    name    VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE users
(
    user_id   BIGSERIAL PRIMARY KEY,
    email     VARCHAR(255) UNIQUE NOT NULL,
    password  VARCHAR(255)        NOT NULL,
    lastname  VARCHAR(100)        NOT NULL,
    firstname VARCHAR(100)        NOT NULL,
    age       INT,
    gender    VARCHAR(20),
    role_id   BIGINT NOT NULL REFERENCES roles(role_id)
);

CREATE TABLE categories
(
    category_id BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE products
(
    product_id BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255)   NOT NULL,
    price      NUMERIC(19, 2) NOT NULL,
    category_id BIGINT NOT NULL REFERENCES categories(category_id)
);

CREATE TABLE orders
(
    order_id      BIGSERIAL PRIMARY KEY,
    user_id       BIGINT    NOT NULL REFERENCES users (user_id),
    purchase_date TIMESTAMP NOT NULL
);

CREATE TABLE order_items
(
    order_item_id     BIGSERIAL PRIMARY KEY,
    order_id          BIGINT         NOT NULL REFERENCES orders (order_id),
    product_id        BIGINT         NOT NULL REFERENCES products (product_id),
    price_at_purchase NUMERIC(19, 2) NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0)
);

CREATE TABLE bucket
(
    bucket_id BIGSERIAL PRIMARY KEY,
    user_id   BIGINT UNIQUE NOT NULL REFERENCES users (user_id)
);

CREATE TABLE bucket_items
(
    bucket_item_id BIGSERIAL PRIMARY KEY,
    bucket_id      BIGINT NOT NULL REFERENCES bucket (bucket_id),
    product_id     BIGINT NOT NULL REFERENCES products (product_id),
    quantity       INT    NOT NULL,

    CONSTRAINT uq_bucket_product
        UNIQUE (bucket_id, product_id)
);
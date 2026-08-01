-- 1. Створюємо 1 мільйон користувачів
INSERT INTO users (email, password, lastname, firstname, age, gender, role_id, is_active)
SELECT
    'user_' || i || '@example.com',
    'hashed_password',
    'Lastname' || (i % 1000),
    'Firstname' || i,
    20 + (i % 50),
    CASE WHEN i % 2 = 0 THEN 'Female' ELSE 'Male' END,
    (i % 3) + 1,
    true
FROM generate_series(1, 1000000) AS i;

-- 2. Створюємо 50 тисяч товарів
INSERT INTO products (name, price, category_id, is_active)
SELECT
    'Product ' || i,
    round(CAST(random() * 500 + 10 as numeric), 2),
    (i % 3) + 1,
    true
FROM generate_series(1, 50000) AS i;

-- 3. Створюємо кошики для користувачів
INSERT INTO bucket (user_id)
SELECT user_id FROM users;

-- 4. Створюємо 2 мільйони замовлень
INSERT INTO orders (user_id, purchase_date)
SELECT
    (random() * 999999 + 1)::bigint,
    NOW() - (random() * interval '365 days')
FROM generate_series(1, 2000000);

-- 5. Створюємо 5 мільйонів елементів замовлень
INSERT INTO order_items (order_id, product_id, price_at_purchase, quantity)
SELECT
    (random() * 1999999 + 1)::bigint,
    (random() * 49999 + 1)::bigint,
    round(CAST(random() * 500 + 10 as numeric), 2),
    (random() * 4 + 1)::int
FROM generate_series(1, 5000000);


EXPLAIN ANALYZE
SELECT o.order_id, o.purchase_date, oi.product_id, oi.quantity, oi.price_at_purchase
FROM orders o
         JOIN order_items oi ON o.order_id = oi.order_id
WHERE o.user_id = 54321;


CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);

EXPLAIN ANALYZE
SELECT o.order_id, o.purchase_date, oi.product_id, oi.quantity, oi.price_at_purchase
FROM orders o
         JOIN order_items oi ON o.order_id = oi.order_id
WHERE o.user_id = 54321;
-- CRUD OPERATIONS --

-- roles --
INSERT INTO roles (name) VALUES ("admin");
SELECT * FROM roles WHERE role_id = 1;
UPDATE roles SET role = "user" WHERE role_id = 1;
DELETE FROM roles WHERE role_id = 1;

-- users --
INSERT INTO users (email, password, lastname, firstname, age, gender) VALUES ("burmalda@gmail.com", "siblings2222", "Alex", "Jordan", 18,"MALE");
SELECT * FROM users WHERE email = "burmalda@gmail.com";
UPDATE users SET email = "user2224@gmail.com";
DELETE FROM users WHERE email = "user2224@gmail.com";

-- CATEGORIES CRUD
INSERT INTO categories (name) VALUES ('Smartphones');
SELECT * FROM categories WHERE category_id = 1;
UPDATE categories SET name = 'Mobile Phones' WHERE category_id = 1;
DELETE FROM categories WHERE category_id = 1;


-- PRODUCTS CRUD
INSERT INTO products (name, price, category_id) VALUES ('Galaxy A36', 399.99, 1);
SELECT * FROM products WHERE product_id = 1;
UPDATE products SET price = 379.99 WHERE product_id = 1;
DELETE FROM products WHERE product_id = 1;


-- ORDERS CRUD
INSERT INTO orders (user_id, purchase_date) VALUES (1, CURRENT_TIMESTAMP);
SELECT * FROM orders WHERE order_id = 1;
UPDATE orders SET purchase_date = CURRENT_TIMESTAMP WHERE order_id = 1;
DELETE FROM orders WHERE order_id = 1;


-- ORDER_ITEMS CRUD
INSERT INTO order_items (order_id, product_id, price_at_purchase, quantity) VALUES (1, 1, 399.99, 2);
SELECT * FROM order_items WHERE order_item_id = 1;
UPDATE order_items SET quantity = 3 WHERE order_item_id = 1;
DELETE FROM order_items WHERE order_item_id = 1;


-- BUCKET CRUD
INSERT INTO bucket (user_id) VALUES (1);
SELECT * FROM bucket WHERE bucket_id = 1;
UPDATE bucket SET user_id = 1 WHERE bucket_id = 1;
DELETE FROM bucket WHERE bucket_id = 1;

-- BUCKET_ITEMS CRUD
INSERT INTO bucket_items (bucket_id, product_id, quantity) VALUES (1, 1, 1);
SELECT * FROM bucket_items WHERE bucket_item_id = 1;
UPDATE bucket_items SET quantity = 2 WHERE bucket_item_id = 1;
DELETE FROM bucket_items WHERE bucket_item_id = 1;


-- marketplace with categories and products
SELECT
    p.product_id,
    p.name AS product_name,
    p.price,
    c.name AS category_name
FROM products p
         JOIN categories c ON p.category_id = c.category_id;

-- user bucket with product name etc.
SELECT
    u.firstname,
    u.lastname,
    p.name AS product_name,
    bi.quantity,
    p.price,
    (bi.quantity * p.price) AS total_item_price
FROM bucket b
         JOIN users u ON b.user_id = u.user_id
         JOIN bucket_items bi ON b.bucket_id = bi.bucket_id
         JOIN products p ON bi.product_id = p.product_id
WHERE u.user_id = 1;

-- pagination, dynamic filters, sort
SELECT
    p.product_id,
    p.name AS product_name,
    p.price,
    c.name AS category_name
FROM products p
         JOIN categories c ON p.category_id = c.category_id
WHERE p.price BETWEEN 100 AND 1000
  AND c.category_id = 1
ORDER BY p.price ASC
    LIMIT 10
OFFSET 0;

--static request (Counting products in each category)
SELECT c.name as category_name,
       COUNT(p.product_id) AS total_products
FROM categories c
LEFT JOIN products p ON c.category_id = p.category_id
GROUP BY c.category_id, c.name;


--top request (top 5 most expensive products)
SELECT
    name,
    price
FROM products
ORDER BY price DESC
    LIMIT 5;



MERGE INTO categories (name) KEY(name) VALUES ('Electronics');
MERGE INTO categories (name) KEY(name) VALUES ('Clothing');
MERGE INTO categories (name) KEY(name) VALUES ('Books');

-- products
MERGE INTO products (name, price, category_id, is_active)
    KEY(name)
    VALUES ('Grid T-Shirt With Logo', 10.00, (SELECT category_id FROM categories WHERE name = 'Clothing'), true);

MERGE INTO products (name, price, category_id, is_active)
    KEY(name)
    VALUES ('Grid "Artificial Intelligence" Book', 5.00, (SELECT category_id FROM categories WHERE name = 'Books'), true);

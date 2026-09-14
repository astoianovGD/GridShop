MERGE INTO roles (name) KEY(name) VALUES ('ADMIN');
MERGE INTO roles (name) KEY(name) VALUES ('STAFF');
MERGE INTO roles (name) KEY(name) VALUES ('USER');

-- admin
MERGE INTO users (email, password, firstname, lastname, age, gender, role_id, is_active)
    KEY(email)
    VALUES ('bestg202@gmail.com', 'burmalda 2008', 'Alex', 'Stoianov', 20, 'MALE', (SELECT role_id FROM roles WHERE name = 'ADMIN'), true);

-- staff
MERGE INTO users (email, password, firstname, lastname, age, gender, role_id, is_active)
    KEY(email)
    VALUES ('vitali@gmail.com', 'burmalda 2008', 'Vitali', 'Chal', 25, 'MALE', (SELECT role_id FROM roles WHERE name = 'STAFF'), true);

MERGE INTO users (email, password, firstname, lastname, age, gender, role_id, is_active)
    KEY(email)
    VALUES ('limstig@gmail.com', 'burmalda 2008', 'Limstig', 'Fernandes', 27, 'MALE', (SELECT role_id FROM roles WHERE name = 'STAFF'), true);

-- users
MERGE INTO users (email, password, firstname, lastname, age, gender, role_id, is_active)
    KEY(email)
    VALUES ('misha@gmail.com', 'burmalda 2008', 'Misha', 'Stoianov', 25, 'MALE', (SELECT role_id FROM roles WHERE name = 'USER'), true);

MERGE INTO users (email, password, firstname, lastname, age, gender, role_id, is_active)
    KEY(email)
    VALUES ('varya@gmail.com', 'burmalda 2008', 'Varya', 'Dotsenko', 18, 'FEMALE', (SELECT role_id FROM roles WHERE name = 'USER'), true);

MERGE INTO roles (name) KEY(name) VALUES ('ADMIN');
MERGE INTO roles (name) KEY(name) VALUES ('STAFF');
MERGE INTO roles (name) KEY(name) VALUES ('USER');

-- admin
MERGE INTO users (email, password, firstname, lastname, age, gender, role_id, is_active)
    KEY(email)
    VALUES ('bestg202@gmail.com', '$2a$05$3Qc6BoSfOYplSqtANJw7DexkE9Hu/EtoKjeRwIUwJn6G52uS4zbrm', 'Alex', 'Stoianov', 20, 'MALE', (SELECT role_id FROM roles WHERE name = 'ADMIN'), true);

-- staff
MERGE INTO users (email, password, firstname, lastname, age, gender, role_id, is_active)
    KEY(email)
    VALUES ('vitali@gmail.com', '$2a$05$3Qc6BoSfOYplSqtANJw7DexkE9Hu/EtoKjeRwIUwJn6G52uS4zbrm', 'Vitali', 'Chal', 25, 'MALE', (SELECT role_id FROM roles WHERE name = 'STAFF'), true);

MERGE INTO users (email, password, firstname, lastname, age, gender, role_id, is_active)
    KEY(email)
    VALUES ('limstig@gmail.com', '$2a$05$3Qc6BoSfOYplSqtANJw7DexkE9Hu/EtoKjeRwIUwJn6G52uS4zbrm', 'Limstig', 'Fernandes', 27, 'MALE', (SELECT role_id FROM roles WHERE name = 'STAFF'), true);

-- users
MERGE INTO users (email, password, firstname, lastname, age, gender, role_id, is_active)
    KEY(email)
    VALUES ('misha@gmail.com', '$2a$05$3Qc6BoSfOYplSqtANJw7DexkE9Hu/EtoKjeRwIUwJn6G52uS4zbrm', 'Misha', 'Stoianov', 25, 'MALE', (SELECT role_id FROM roles WHERE name = 'USER'), true);

MERGE INTO users (email, password, firstname, lastname, age, gender, role_id, is_active)
    KEY(email)
    VALUES ('varya@gmail.com', '$2a$05$3Qc6BoSfOYplSqtANJw7DexkE9Hu/EtoKjeRwIUwJn6G52uS4zbrm', 'Varya', 'Dotsenko', 18, 'FEMALE', (SELECT role_id FROM roles WHERE name = 'USER'), true);

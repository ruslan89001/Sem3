CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL,
                                     password VARCHAR(255) NOT NULL,
                                     email VARCHAR(255) UNIQUE NOT NULL,
                                     role VARCHAR(50) NOT NULL,
                                     token UUID DEFAULT gen_random_uuid()
);

CREATE TABLE IF NOT EXISTS spaces (
                                      id SERIAL PRIMARY KEY,
                                      name VARCHAR(255) NOT NULL,
                                      description TEXT NOT NULL,
                                      price DECIMAL(10, 2) NOT NULL,
                                      availability BOOLEAN DEFAULT TRUE,
                                      location VARCHAR(255) NOT NULL,
                                      image VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS bookings (
                                        id SERIAL PRIMARY KEY,
                                        user_id INT NOT NULL,
                                        space_id INT NOT NULL,
                                        start_time TIMESTAMP NOT NULL,
                                        end_time TIMESTAMP NOT NULL,
                                        status VARCHAR(50) DEFAULT 'pending',
                                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                        FOREIGN KEY (space_id) REFERENCES spaces(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews (
                                       id SERIAL PRIMARY KEY,
                                       user_id INT NOT NULL,
                                       space_id INT NOT NULL,
                                       rating INT CHECK (rating >= 1 AND rating <= 5),
                                       comment TEXT,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                       FOREIGN KEY (space_id) REFERENCES spaces(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS working_hours (
                                             id SERIAL PRIMARY KEY,
                                             space_id INT NOT NULL,
                                             weekday VARCHAR(15) NOT NULL,
                                             start_time TIMESTAMP NOT NULL,
                                             end_time TIMESTAMP NOT NULL,
                                             FOREIGN KEY (space_id) REFERENCES spaces(id) ON DELETE CASCADE
);

-- Вставка тестового пользователя
INSERT INTO users (username, password, email, role, token)
VALUES (
           'admin',
           '6d4525c2a21f9be1cca9e41f3aa402e0765ee5fcc3e7fea34a169b1730ae386e',
           'admin@coworking.com',
           'admin',
           gen_random_uuid()
       );
-- Вставка тестовых коворкинг-пространств
INSERT INTO spaces (name, description, price, location, image)
VALUES ('Коворкинг Пространство 1', 'Просторное пространство для работы с удобствами.', 500.00, 'Москва, ул. Ленина, 10', 'image1.jpg'),
       ('Коворкинг Пространство 2', 'Комфортное рабочее пространство с высокоскоростным интернетом.', 450.00, 'Москва, ул. Пушкина, 20', 'image2.jpg');

-- Вставка тестовых бронирований
INSERT INTO bookings (user_id, space_id, start_time, end_time, status)
VALUES (1, 1, '2025-01-10 09:00:00', '2025-01-10 17:00:00', 'confirmed');

-- Вставка тестовых отзывов
INSERT INTO reviews (user_id, space_id, rating, comment)
VALUES (1, 1, 5, 'Отличное место для работы! Рекомендую!');

-- Вставка тестовых рабочих часов
INSERT INTO working_hours (space_id, weekday, start_time, end_time)
VALUES (1, 'Понедельник', '2025-01-05 09:00:00', '2025-01-05 18:00:00'),
       (1, 'Вторник', '2025-01-06 09:00:00', '2025-01-06 18:00:00');


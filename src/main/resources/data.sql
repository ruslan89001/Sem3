-- Создание пользователя admin
INSERT INTO users (username, password, email, role) 
VALUES ('admin', 'admin', 'admin@example.com', 'admin')
ON DUPLICATE KEY UPDATE username=username;

-- Создание тестовых пространств
INSERT INTO spaces (name, description, location, price, status, capacity) 
VALUES 
('Офис 1', 'Уютный офис для 2-4 человек', 'ул. Ленина, 1', 1000, 'available', 4),
('Конференц-зал', 'Большой зал для встреч', 'ул. Пушкина, 10', 2000, 'available', 10),
('Коворкинг', 'Открытое пространство для работы', 'пр. Мира, 25', 500, 'available', 20)
ON DUPLICATE KEY UPDATE name=name; 
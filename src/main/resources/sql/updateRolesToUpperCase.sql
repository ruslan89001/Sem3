-- Обновляем роли в верхний регистр
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
UPDATE users SET role = 'USER' WHERE username != 'admin' AND role = 'user';

-- Проверяем результат
SELECT id, username, email, role, password FROM users; 
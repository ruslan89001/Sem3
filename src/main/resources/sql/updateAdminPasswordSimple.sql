-- Обновляем пароль админа на простой текст "admin" для тестирования
UPDATE users 
SET password = 'admin' 
WHERE username = 'admin';

-- Обновляем роли в верхний регистр
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
UPDATE users SET role = 'USER' WHERE username != 'admin' AND role = 'user';

-- Проверяем результат
SELECT id, username, email, role, password FROM users WHERE username = 'admin'; 
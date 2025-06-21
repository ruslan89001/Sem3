-- Обновляем пароль админа на BCrypt-хеш пароля "admin"
-- BCrypt-хеш для пароля "admin": $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa
UPDATE users 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa' 
WHERE username = 'admin';

-- Проверяем результат
SELECT id, username, email, role, password FROM users WHERE username = 'admin'; 
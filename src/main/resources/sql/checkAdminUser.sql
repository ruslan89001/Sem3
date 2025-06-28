SELECT id, username, email, role, password FROM users WHERE username = 'admin';

SELECT id, username, email, role, password FROM users;

UPDATE users 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa' 
WHERE username = 'admin';

SELECT id, username, email, role FROM users WHERE username = 'admin';

SELECT id, username, email, role, password FROM users WHERE username = 'admin'; 

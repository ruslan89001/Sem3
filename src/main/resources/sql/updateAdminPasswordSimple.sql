UPDATE users 
SET password = 'admin' 
WHERE username = 'admin';

UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
UPDATE users SET role = 'USER' WHERE username != 'admin' AND role = 'user';

SELECT id, username, email, role, password FROM users WHERE username = 'admin'; 

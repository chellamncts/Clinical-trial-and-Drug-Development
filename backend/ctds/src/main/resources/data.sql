UPDATE user
SET password_hash = 'admin@123',
    role = 'ADMIN',
    active = 1
WHERE username = 'admin';

INSERT INTO user (username, password_hash, role, active)
SELECT 'admin', 'admin@123', 'ADMIN', 1
WHERE NOT EXISTS (
    SELECT 1 FROM user WHERE username = 'admin'
);


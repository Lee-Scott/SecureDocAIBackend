-- Data insertion here this runs after schema.sql
INSERT INTO users (id, user_id, first_name, last_name, email, phone, bio,
            reference_id, image_url, created_by, updated_by, created_at, updated_at,
            account_non_expired, account_non_locked, enabled, mfa)
VALUES (0, '023a7479-e7a7-079f-a766fe25eca9', 'System', 'System', 'System@gmail.com', '1234567890', 'This is the system', '023a7479-e7a7-079f-a766fe25eca9',
'https://cdn-icons-png.flaticon.com/512/149/149071.png', '0', '0', '2023-08-01 00:00:00', '2023-08-01 00:00:00', TRUE, TRUE, FALSE, FALSE);
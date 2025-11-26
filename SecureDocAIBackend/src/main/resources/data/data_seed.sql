BEGIN;

-- Insert a system user (nullable created_by/updated_by)
INSERT INTO users (user_id, first_name, last_name, email, phone, bio, reference_id, image_url, created_by, updated_by, account_non_expired, account_non_locked, enabled, mfa)
VALUES (
  '023a7479-e7a7-079f-a766fe25eca9',
  'System',
  'System',
  'system@example.com',
  '0000000000',
  'System account',
  '023a7479-e7a7-079f-a766fe25eca9',
  'https://cdn-icons-png.flaticon.com/512/149/149071.png',
  NULL,
  NULL,
  TRUE,
  TRUE,
  TRUE,
  FALSE
)
ON CONFLICT (user_id) DO NOTHING;

-- Ensure we can reference system user's id
-- Get the system user id if it exists (for inserting created_by/updated_by if you want)
-- We will use it below to mark created_by for seeded roles.

-- Insert default roles
INSERT INTO roles (authorities, name, reference_id, created_by, updated_by)
VALUES
  ('ROLE_USER', 'USER', 'role-user-ref', (SELECT id FROM users WHERE user_id = '023a7479-e7a7-079f-a766fe25eca9' LIMIT 1), (SELECT id FROM users WHERE user_id = '023a7479-e7a7-079f-a766fe25eca9' LIMIT 1)),
  ('ROLE_ADMIN', 'ADMIN', 'role-admin-ref', (SELECT id FROM users WHERE user_id = '023a7479-e7a7-079f-a766fe25eca9' LIMIT 1), (SELECT id FROM users WHERE user_id = '023a7479-e7a7-079f-a766fe25eca9' LIMIT 1)),
  ('ROLE_SUPER_ADMIN', 'SUPER_ADMIN', 'role-superadmin-ref', (SELECT id FROM users WHERE user_id = '023a7479-e7a7-079f-a766fe25eca9' LIMIT 1), (SELECT id FROM users WHERE user_id = '023a7479-e7a7-079f-a766fe25eca9' LIMIT 1)),
  ('ROLE_MANAGER', 'MANAGER', 'role-manager-ref', (SELECT id FROM users WHERE user_id = '023a7479-e7a7-079f-a766fe25eca9' LIMIT 1), (SELECT id FROM users WHERE user_id = '023a7479-e7a7-079f-a766fe25eca9' LIMIT 1)),
  ('ROLE_AI_AGENT', 'AI_AGENT', 'role-aiagent-ref', (SELECT id FROM users WHERE user_id = '023a7479-e7a7-079f-a766fe25eca9' LIMIT 1), (SELECT id FROM users WHERE user_id = '023a7479-e7a7-079f-a766fe25eca9' LIMIT 1))
ON CONFLICT (name) DO NOTHING;

-- Link system user to a default role (e.g., SUPER_ADMIN)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'SUPER_ADMIN'
WHERE u.user_id = '023a7479-e7a7-079f-a766fe25eca9'
ON CONFLICT (user_id, role_id) DO NOTHING;

COMMIT;

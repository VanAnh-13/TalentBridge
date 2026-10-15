-- The web UI decides which workspace a user may open (/admin, /recruiter) from permissions in the token,
-- exactly like it does for API calls. They were never seeded, so no one could open either workspace
-- without first creating them by hand. They are not API endpoints, hence module 'UI'.
--   GET /admin     -> ADMIN
--   GET /recruiter -> RECRUITER

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /admin', '/admin', 'GET', 'UI', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/admin' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /recruiter', '/recruiter', 'GET', 'UI', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/recruiter' AND x.method = 'GET');

INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.method = 'GET' AND p.api_path = '/admin'
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.method = 'GET' AND p.api_path = '/recruiter'
WHERE r.name = 'RECRUITER'
  AND NOT EXISTS (SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

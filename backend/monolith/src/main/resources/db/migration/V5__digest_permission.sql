-- Sending a job digest to a subscriber used to be callable by anyone (the endpoint was not protected).
-- It is now guarded by a permission that only the ADMIN role holds.

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'POST /emails/job-recommendation/{email}', '/emails/job-recommendation/{email}', 'POST', 'EMAIL', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/emails/job-recommendation/{email}' AND x.method = 'POST');

INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p
  ON p.method = 'POST' AND p.api_path = '/emails/job-recommendation/{email}'
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

-- V2: reference data (roles, permissions, role->permission). Idempotent: safe on databases that
-- already contain this data from the former data.sql.

INSERT INTO roles (name, description, active, created_at, updated_at, created_by, modified_by)
SELECT 'ADMIN', 'Quyền quản trị hệ thống', true, NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM roles r WHERE r.name = 'ADMIN');

INSERT INTO roles (name, description, active, created_at, updated_at, created_by, modified_by)
SELECT 'RECRUITER', 'Nhà tuyển dụng - Quản lý tin tuyển dụng và hồ sơ ứng viên', true, NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM roles r WHERE r.name = 'RECRUITER');

INSERT INTO roles (name, description, active, created_at, updated_at, created_by, modified_by)
SELECT 'USER', 'Người dùng - Ứng tuyển việc làm', true, NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM roles r WHERE r.name = 'USER');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Create Role', '/roles', 'POST', 'ROLE', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/roles' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Get Role List', '/roles', 'GET', 'ROLE', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/roles' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Update Role', '/roles/{id}', 'PUT', 'ROLE', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/roles/{id}' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Delete Role', '/roles/{id}', 'DELETE', 'ROLE', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/roles/{id}' AND x.method = 'DELETE');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Create Permission', '/permissions', 'POST', 'PERMISSION', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/permissions' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Get Permission List', '/permissions', 'GET', 'PERMISSION', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/permissions' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Update Permission', '/permissions/{id}', 'PUT', 'PERMISSION', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/permissions/{id}' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Delete Permission', '/permissions/{id}', 'DELETE', 'PERMISSION', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/permissions/{id}' AND x.method = 'DELETE');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Create User', '/users', 'POST', 'USER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/users' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Get User List', '/users', 'GET', 'USER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/users' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Get User by ID', '/users/{id}', 'GET', 'USER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/users/{id}' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Update User', '/users', 'PUT', 'USER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/users' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Delete User', '/users/{id}', 'DELETE', 'USER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/users/{id}' AND x.method = 'DELETE');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Create Company', '/companies', 'POST', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Get Company List', '/companies', 'GET', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Get Company by ID', '/companies/{id}', 'GET', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies/{id}' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Update Company', '/companies/{id}', 'PUT', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies/{id}' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Delete Company', '/companies/{id}', 'DELETE', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies/{id}' AND x.method = 'DELETE');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Create Job', '/jobs', 'POST', 'JOB', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/jobs' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Get Job List', '/jobs', 'GET', 'JOB', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/jobs' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Get Job by ID', '/jobs/{id}', 'GET', 'JOB', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/jobs/{id}' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Update Job', '/jobs/{id}', 'PUT', 'JOB', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/jobs/{id}' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Delete Job', '/jobs/{id}', 'DELETE', 'JOB', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/jobs/{id}' AND x.method = 'DELETE');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Create Skill', '/skills', 'POST', 'SKILL', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/skills' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Get Skill List', '/skills', 'GET', 'SKILL', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/skills' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Update Skill', '/skills/{id}', 'PUT', 'SKILL', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/skills/{id}' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Delete Skill', '/skills/{id}', 'DELETE', 'SKILL', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/skills/{id}' AND x.method = 'DELETE');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Get Resume List', '/resumes', 'GET', 'RESUME', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/resumes' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Update Resume Status', '/resumes/status', 'PUT', 'RESUME', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/resumes/status' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Get Subscriber List', '/subscribers', 'GET', 'SUBSCRIBER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/subscribers' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'Delete Subscriber', '/subscribers/{id}', 'DELETE', 'SUBSCRIBER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/subscribers/{id}' AND x.method = 'DELETE');

-- ADMIN: every permission
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

-- RECRUITER: company, job and resume management
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'RECRUITER'
  AND p.api_path IN ('/companies', '/companies/{id}', '/jobs', '/jobs/{id}', '/resumes', '/resumes/status')
  AND NOT EXISTS (SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

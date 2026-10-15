-- V4: permission model redesign.
--
-- Problems fixed:
--  * The seed granted RECRUITER path-based permissions ('PUT /jobs/{id}', 'DELETE /companies/{id}', ...) whose
--    services perform no ownership check, so a recruiter could modify any company's data.
--  * Recruiters/users lacked the permissions of the endpoints they really use (/companies/me, /jobs/company,
--    /resumes/company, /resumes/me, /subscribers/me, public reads while logged in, ...).
--  * Seeded permission names did not match the controllers ('PUT /skills/{id}' vs 'PUT /skills', ...).
--
-- 1) make the catalog match the controllers, 2) drop seeded rows that authorize nothing,
-- 3) reset the assignments of the three built-in roles to the explicit matrix below (custom roles are untouched).

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'POST /roles', '/roles', 'POST', 'ROLE', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/roles' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /roles', '/roles', 'GET', 'ROLE', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/roles' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'PUT /roles/{id}', '/roles/{id}', 'PUT', 'ROLE', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/roles/{id}' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'DELETE /roles/{id}', '/roles/{id}', 'DELETE', 'ROLE', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/roles/{id}' AND x.method = 'DELETE');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'POST /permissions', '/permissions', 'POST', 'PERMISSION', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/permissions' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /permissions', '/permissions', 'GET', 'PERMISSION', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/permissions' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /permissions/all', '/permissions/all', 'GET', 'PERMISSION', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/permissions/all' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'PUT /permissions/{id}', '/permissions/{id}', 'PUT', 'PERMISSION', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/permissions/{id}' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'DELETE /permissions/{id}', '/permissions/{id}', 'DELETE', 'PERMISSION', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/permissions/{id}' AND x.method = 'DELETE');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'POST /users', '/users', 'POST', 'USER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/users' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /users', '/users', 'GET', 'USER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/users' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /users/{id}', '/users/{id}', 'GET', 'USER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/users/{id}' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'PUT /users', '/users', 'PUT', 'USER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/users' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'DELETE /users/{id}', '/users/{id}', 'DELETE', 'USER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/users/{id}' AND x.method = 'DELETE');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'POST /companies', '/companies', 'POST', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /companies', '/companies', 'GET', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /companies/with-jobs-count', '/companies/with-jobs-count', 'GET', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies/with-jobs-count' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /companies/{id}', '/companies/{id}', 'GET', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies/{id}' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'PUT /companies/{id}', '/companies/{id}', 'PUT', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies/{id}' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'DELETE /companies/{id}', '/companies/{id}', 'DELETE', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies/{id}' AND x.method = 'DELETE');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /companies/me', '/companies/me', 'GET', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies/me' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'POST /companies/me', '/companies/me', 'POST', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies/me' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'PUT /companies/me', '/companies/me', 'PUT', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies/me' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /companies/me/users', '/companies/me/users', 'GET', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies/me/users' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'POST /companies/me/users', '/companies/me/users', 'POST', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies/me/users' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'PUT /companies/me/users', '/companies/me/users', 'PUT', 'COMPANY', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/companies/me/users' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'POST /jobs', '/jobs', 'POST', 'JOB', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/jobs' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /jobs', '/jobs', 'GET', 'JOB', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/jobs' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /jobs/{id}', '/jobs/{id}', 'GET', 'JOB', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/jobs/{id}' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /jobs/companies/{id}', '/jobs/companies/{id}', 'GET', 'JOB', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/jobs/companies/{id}' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'PUT /jobs/{id}', '/jobs/{id}', 'PUT', 'JOB', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/jobs/{id}' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'DELETE /jobs/{id}', '/jobs/{id}', 'DELETE', 'JOB', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/jobs/{id}' AND x.method = 'DELETE');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /jobs/company', '/jobs/company', 'GET', 'JOB', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/jobs/company' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'POST /jobs/company', '/jobs/company', 'POST', 'JOB', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/jobs/company' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'PUT /jobs/company/{id}', '/jobs/company/{id}', 'PUT', 'JOB', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/jobs/company/{id}' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'DELETE /jobs/company/{id}', '/jobs/company/{id}', 'DELETE', 'JOB', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/jobs/company/{id}' AND x.method = 'DELETE');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'POST /skills', '/skills', 'POST', 'SKILL', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/skills' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /skills', '/skills', 'GET', 'SKILL', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/skills' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /skills/{id}', '/skills/{id}', 'GET', 'SKILL', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/skills/{id}' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'PUT /skills', '/skills', 'PUT', 'SKILL', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/skills' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'DELETE /skills/{id}', '/skills/{id}', 'DELETE', 'SKILL', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/skills/{id}' AND x.method = 'DELETE');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'POST /resumes', '/resumes', 'POST', 'RESUME', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/resumes' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /resumes', '/resumes', 'GET', 'RESUME', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/resumes' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'PUT /resumes/status', '/resumes/status', 'PUT', 'RESUME', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/resumes/status' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /resumes/company', '/resumes/company', 'GET', 'RESUME', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/resumes/company' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'PUT /resumes/company/status', '/resumes/company/status', 'PUT', 'RESUME', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/resumes/company/status' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /resumes/me', '/resumes/me', 'GET', 'RESUME', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/resumes/me' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'DELETE /resumes/me/jobs/{jobId}', '/resumes/me/jobs/{jobId}', 'DELETE', 'RESUME', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/resumes/me/jobs/{jobId}' AND x.method = 'DELETE');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'PUT /resumes/me/file/{id}', '/resumes/me/file/{id}', 'PUT', 'RESUME', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/resumes/me/file/{id}' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /resumes/file/{id}', '/resumes/file/{id}', 'GET', 'RESUME', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/resumes/file/{id}' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'POST /subscribers/me', '/subscribers/me', 'POST', 'SUBSCRIBER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/subscribers/me' AND x.method = 'POST');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /subscribers/me', '/subscribers/me', 'GET', 'SUBSCRIBER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/subscribers/me' AND x.method = 'GET');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'PUT /subscribers/me', '/subscribers/me', 'PUT', 'SUBSCRIBER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/subscribers/me' AND x.method = 'PUT');

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'DELETE /subscribers/me', '/subscribers/me', 'DELETE', 'SUBSCRIBER', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/subscribers/me' AND x.method = 'DELETE');

-- seeded rows that no endpoint checks
DELETE FROM roles_permissions WHERE permission_id IN (
  SELECT id FROM (SELECT id FROM permissions
    WHERE (method = 'PUT' AND api_path = '/skills/{id}')
       OR (method = 'GET' AND api_path = '/subscribers')
       OR (method = 'DELETE' AND api_path = '/subscribers/{id}')) d);
DELETE FROM permissions
 WHERE (method = 'PUT' AND api_path = '/skills/{id}')
    OR (method = 'GET' AND api_path = '/subscribers')
    OR (method = 'DELETE' AND api_path = '/subscribers/{id}');

DELETE FROM roles_permissions WHERE role_id IN (SELECT id FROM roles WHERE name IN ('ADMIN', 'RECRUITER', 'USER'));

-- ADMIN: the whole catalog
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'ADMIN';

-- RECRUITER
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p
  ON (p.method, p.api_path) IN (
    ('GET', '/companies'),
    ('GET', '/companies/with-jobs-count'),
    ('GET', '/companies/{id}'),
    ('GET', '/jobs'),
    ('GET', '/jobs/{id}'),
    ('GET', '/jobs/companies/{id}'),
    ('GET', '/skills'),
    ('GET', '/skills/{id}'),
    ('GET', '/companies/me'),
    ('POST', '/companies/me'),
    ('PUT', '/companies/me'),
    ('GET', '/companies/me/users'),
    ('POST', '/companies/me/users'),
    ('PUT', '/companies/me/users'),
    ('GET', '/jobs/company'),
    ('POST', '/jobs/company'),
    ('PUT', '/jobs/company/{id}'),
    ('DELETE', '/jobs/company/{id}'),
    ('GET', '/resumes/company'),
    ('PUT', '/resumes/company/status'),
    ('GET', '/resumes/file/{id}'))
WHERE r.name = 'RECRUITER';

-- USER
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p
  ON (p.method, p.api_path) IN (
    ('GET', '/companies'),
    ('GET', '/companies/with-jobs-count'),
    ('GET', '/companies/{id}'),
    ('GET', '/jobs'),
    ('GET', '/jobs/{id}'),
    ('GET', '/jobs/companies/{id}'),
    ('GET', '/skills'),
    ('GET', '/skills/{id}'),
    ('POST', '/resumes'),
    ('GET', '/resumes/me'),
    ('DELETE', '/resumes/me/jobs/{jobId}'),
    ('PUT', '/resumes/me/file/{id}'),
    ('GET', '/resumes/file/{id}'),
    ('POST', '/subscribers/me'),
    ('GET', '/subscribers/me'),
    ('PUT', '/subscribers/me'),
    ('DELETE', '/subscribers/me'))
WHERE r.name = 'USER';

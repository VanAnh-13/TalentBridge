-- =====================================================
-- Initialize Roles
-- =====================================================
INSERT INTO roles (name, description, active, created_at, updated_at, created_by, modified_by)
VALUES 
    ('ADMIN', 'Quyền quản trị hệ thống', true, NOW(), NOW(), 'system', 'system'),
    ('RECRUITER', 'Nhà tuyển dụng - Quản lý tin tuyển dụng và hồ sơ ứng viên', true, NOW(), NOW(), 'system', 'system'),
    ('USER', 'Người dùng - Ứng tuyển việc làm', true, NOW(), NOW(), 'system', 'system')
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    active = VALUES(active),
    updated_at = NOW(),
    modified_by = VALUES(modified_by);

-- =====================================================
-- Initialize Permissions - Role Management (ADMIN ONLY)
-- =====================================================
INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
VALUES 
    -- Role Management Permissions (ADMIN ONLY)
    ('Create Role', '/roles', 'POST', 'ROLE', NOW(), NOW(), 'system', 'system'),
    ('Get Role List', '/roles', 'GET', 'ROLE', NOW(), NOW(), 'system', 'system'),
    ('Update Role', '/roles/{id}', 'PUT', 'ROLE', NOW(), NOW(), 'system', 'system'),
    ('Delete Role', '/roles/{id}', 'DELETE', 'ROLE', NOW(), NOW(), 'system', 'system'),
    
    -- Permission Management (ADMIN ONLY)
    ('Create Permission', '/permissions', 'POST', 'PERMISSION', NOW(), NOW(), 'system', 'system'),
    ('Get Permission List', '/permissions', 'GET', 'PERMISSION', NOW(), NOW(), 'system', 'system'),
    ('Update Permission', '/permissions/{id}', 'PUT', 'PERMISSION', NOW(), NOW(), 'system', 'system'),
    ('Delete Permission', '/permissions/{id}', 'DELETE', 'PERMISSION', NOW(), NOW(), 'system', 'system'),
    
    -- User Management (ADMIN ONLY)
    ('Create User', '/users', 'POST', 'USER', NOW(), NOW(), 'system', 'system'),
    ('Get User List', '/users', 'GET', 'USER', NOW(), NOW(), 'system', 'system'),
    ('Get User by ID', '/users/{id}', 'GET', 'USER', NOW(), NOW(), 'system', 'system'),
    ('Update User', '/users', 'PUT', 'USER', NOW(), NOW(), 'system', 'system'),
    ('Delete User', '/users/{id}', 'DELETE', 'USER', NOW(), NOW(), 'system', 'system'),
    
    -- Company Management (ADMIN and RECRUITER)
    ('Create Company', '/companies', 'POST', 'COMPANY', NOW(), NOW(), 'system', 'system'),
    ('Get Company List', '/companies', 'GET', 'COMPANY', NOW(), NOW(), 'system', 'system'),
    ('Get Company by ID', '/companies/{id}', 'GET', 'COMPANY', NOW(), NOW(), 'system', 'system'),
    ('Update Company', '/companies/{id}', 'PUT', 'COMPANY', NOW(), NOW(), 'system', 'system'),
    ('Delete Company', '/companies/{id}', 'DELETE', 'COMPANY', NOW(), NOW(), 'system', 'system'),
    
    -- Job Management (ADMIN and RECRUITER)
    ('Create Job', '/jobs', 'POST', 'JOB', NOW(), NOW(), 'system', 'system'),
    ('Get Job List', '/jobs', 'GET', 'JOB', NOW(), NOW(), 'system', 'system'),
    ('Get Job by ID', '/jobs/{id}', 'GET', 'JOB', NOW(), NOW(), 'system', 'system'),
    ('Update Job', '/jobs/{id}', 'PUT', 'JOB', NOW(), NOW(), 'system', 'system'),
    ('Delete Job', '/jobs/{id}', 'DELETE', 'JOB', NOW(), NOW(), 'system', 'system'),
    
    -- Skill Management (ADMIN ONLY)
    ('Create Skill', '/skills', 'POST', 'SKILL', NOW(), NOW(), 'system', 'system'),
    ('Get Skill List', '/skills', 'GET', 'SKILL', NOW(), NOW(), 'system', 'system'),
    ('Update Skill', '/skills/{id}', 'PUT', 'SKILL', NOW(), NOW(), 'system', 'system'),
    ('Delete Skill', '/skills/{id}', 'DELETE', 'SKILL', NOW(), NOW(), 'system', 'system'),
    
    -- Resume Management (ADMIN and RECRUITER)
    ('Get Resume List', '/resumes', 'GET', 'RESUME', NOW(), NOW(), 'system', 'system'),
    ('Update Resume Status', '/resumes/status', 'PUT', 'RESUME', NOW(), NOW(), 'system', 'system'),
    
    -- Subscriber Management (ADMIN ONLY)
    ('Get Subscriber List', '/subscribers', 'GET', 'SUBSCRIBER', NOW(), NOW(), 'system', 'system'),
    ('Delete Subscriber', '/subscribers/{id}', 'DELETE', 'SUBSCRIBER', NOW(), NOW(), 'system', 'system');

-- =====================================================
-- Assign Permissions to ADMIN Role (ALL PERMISSIONS)
-- =====================================================
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
ON DUPLICATE KEY UPDATE
    role_id = VALUES(role_id),
    permission_id = VALUES(permission_id);

-- =====================================================
-- Assign Permissions to RECRUITER Role (LIMITED)
-- =====================================================
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'RECRUITER'
AND p.api_path IN (
    -- Company management
    '/companies', '/companies/{id}',
    -- Job management  
    '/jobs', '/jobs/{id}',
    -- Resume viewing
    '/resumes', '/resumes/status'
)
ON DUPLICATE KEY UPDATE
    role_id = VALUES(role_id),
    permission_id = VALUES(permission_id);

-- =====================================================
-- USER Role has no admin permissions (can only access public endpoints)
-- =====================================================

-- NOTE: no default admin is seeded. Create one with app.bootstrap.admin-email/admin-password
-- (env BOOTSTRAP_ADMIN_EMAIL / BOOTSTRAP_ADMIN_PASSWORD).

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
    
    -- Resume Self Management (USER)
    ('Create Resume', '/resumes', 'POST', 'RESUME', NOW(), NOW(), 'system', 'system'),
    ('Get Self Resumes', '/resumes/me', 'GET', 'RESUME', NOW(), NOW(), 'system', 'system'),
    ('Delete Self Resume', '/resumes/me/jobs/{jobId}', 'DELETE', 'RESUME', NOW(), NOW(), 'system', 'system'),
    ('Update Self Resume File', '/resumes/me/file/{id}', 'PUT', 'RESUME', NOW(), NOW(), 'system', 'system'),
    ('Get Resume File', '/resumes/file/{id}', 'GET', 'RESUME', NOW(), NOW(), 'system', 'system'),
    
    -- Subscriber Management (ADMIN ONLY)
    ('Get Subscriber List', '/subscribers', 'GET', 'SUBSCRIBER', NOW(), NOW(), 'system', 'system'),
    ('Delete Subscriber', '/subscribers/{id}', 'DELETE', 'SUBSCRIBER', NOW(), NOW(), 'system', 'system'),
    
    -- Subscriber Self Management (USER)
    ('Get Self Subscriber', '/subscribers/me', 'GET', 'SUBSCRIBER', NOW(), NOW(), 'system', 'system'),
    ('Create Self Subscriber', '/subscribers/me', 'POST', 'SUBSCRIBER', NOW(), NOW(), 'system', 'system'),
    ('Update Self Subscriber', '/subscribers/me', 'PUT', 'SUBSCRIBER', NOW(), NOW(), 'system', 'system'),
    ('Delete Self Subscriber', '/subscribers/me', 'DELETE', 'SUBSCRIBER', NOW(), NOW(), 'system', 'system'),
    
    -- RECRUITER Page Access Permission
    ('Access Recruiter Dashboard', '/recruiter', 'GET', 'RECRUITER', NOW(), NOW(), 'system', 'system'),
    ('Get Self Company', '/companies/me', 'GET', 'COMPANY', NOW(), NOW(), 'system', 'system'),
    ('Update Self Company', '/companies/me', 'PUT', 'COMPANY', NOW(), NOW(), 'system', 'system'),
    ('Create Self Company', '/companies/me', 'POST', 'COMPANY', NOW(), NOW(), 'system', 'system'),
    ('Get Self Company Users', '/companies/me/users', 'GET', 'COMPANY', NOW(), NOW(), 'system', 'system'),
    ('Add Self Company User', '/companies/me/users', 'POST', 'COMPANY', NOW(), NOW(), 'system', 'system'),
    ('Update Self Company User', '/companies/me/users', 'PUT', 'COMPANY', NOW(), NOW(), 'system', 'system'),
    ('Get Company Resumes', '/resumes/company', 'GET', 'RESUME', NOW(), NOW(), 'system', 'system'),
    ('Get Company Jobs', '/jobs/company', 'GET', 'JOB', NOW(), NOW(), 'system', 'system'),
    ('Create Company Job', '/jobs/company', 'POST', 'JOB', NOW(), NOW(), 'system', 'system'),
    ('Update Company Job', '/jobs/company/{id}', 'PUT', 'JOB', NOW(), NOW(), 'system', 'system'),
    ('Delete Company Job', '/jobs/company/{id}', 'DELETE', 'JOB', NOW(), NOW(), 'system', 'system'),
    
    -- ADMIN Page Access Permission
    ('Access Admin Dashboard', '/admin', 'GET', 'ADMIN', NOW(), NOW(), 'system', 'system')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    module = VALUES(module),
    updated_at = NOW();

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
    -- Recruiter page access
    '/recruiter',
    -- Self Company management
    '/companies/me', '/companies/me/users',
    -- Company management
    '/companies', '/companies/{id}',
    -- Job management (self company)
    '/jobs', '/jobs/{id}', '/jobs/company', '/jobs/company/{id}',
    -- Resume viewing (company)
    '/resumes', '/resumes/status', '/resumes/company',
    -- Skills
    '/skills'
)
ON DUPLICATE KEY UPDATE
    role_id = VALUES(role_id),
    permission_id = VALUES(permission_id);

-- =====================================================
-- Assign Permissions to USER Role
-- =====================================================
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'USER'
AND p.api_path IN (
    -- Subscriber self management
    '/subscribers/me',
    -- Resume self management
    '/resumes',
    '/resumes/me',
    '/resumes/me/jobs/{jobId}',
    '/resumes/me/file/{id}',
    '/resumes/file/{id}'
)
ON DUPLICATE KEY UPDATE
    role_id = VALUES(role_id),
    permission_id = VALUES(permission_id);

-- =====================================================
-- Create Default Admin User
-- Password: admin123 (hashed with BCrypt)
-- =====================================================
INSERT INTO users (email, name, password, dob, address, gender, role_id, created_at, updated_at, created_by, modified_by)
SELECT 'admin@talentbridge.com', 'System Admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1990-01-01', 'System', 'OTHER', r.id, NOW(), NOW(), 'system', 'system'
FROM roles r
WHERE r.name = 'ADMIN'
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    password = VALUES(password),
    dob = VALUES(dob),
    address = VALUES(address),
    gender = VALUES(gender),
    role_id = VALUES(role_id),
    updated_at = NOW(),
    modified_by = VALUES(modified_by);

-- =====================================================
-- Create Test Recruiter User
-- =====================================================
INSERT INTO users (email, name, password, dob, address, gender, role_id, company_id, created_at, updated_at, created_by, modified_by)
SELECT 'testrecruiter@talentbridge.com', 'Test Recruiter', '', '1990-01-01', 'Test Address', 'MALE', id, NULL, NOW(), NOW(), 'system', 'system'
FROM roles WHERE name = 'RECRUITER'
ON DUPLICATE KEY UPDATE updated_at = NOW();


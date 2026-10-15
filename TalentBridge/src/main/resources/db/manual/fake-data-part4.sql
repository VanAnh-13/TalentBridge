-- =====================================================
-- FAKE DATA FOR TALENTBRIDGE - PART 4
-- Job-Skill relationships and Resumes
-- =====================================================

-- =====================================================
-- JOB_SKILL relationships (map skills to jobs)
-- =====================================================
-- Assuming job IDs start from 1 and skill IDs start from 1
INSERT INTO job_skill (job_id, skill_id)
SELECT j.id, s.id FROM 
    (SELECT id FROM jobs WHERE name = 'Senior Java Developer' LIMIT 1) j,
    (SELECT id FROM skills WHERE name IN ('Java', 'Spring Boot', 'MySQL', 'Docker', 'Microservices')) s
ON DUPLICATE KEY UPDATE job_id = job_id;

INSERT INTO job_skill (job_id, skill_id)
SELECT j.id, s.id FROM 
    (SELECT id FROM jobs WHERE name = 'React Frontend Developer' LIMIT 1) j,
    (SELECT id FROM skills WHERE name IN ('React', 'TypeScript', 'JavaScript', 'GraphQL')) s
ON DUPLICATE KEY UPDATE job_id = job_id;

INSERT INTO job_skill (job_id, skill_id)
SELECT j.id, s.id FROM 
    (SELECT id FROM jobs WHERE name = 'Python AI Engineer' LIMIT 1) j,
    (SELECT id FROM skills WHERE name IN ('Python', 'AWS', 'Docker')) s
ON DUPLICATE KEY UPDATE job_id = job_id;

INSERT INTO job_skill (job_id, skill_id)
SELECT j.id, s.id FROM 
    (SELECT id FROM jobs WHERE name = 'DevOps Engineer' LIMIT 1) j,
    (SELECT id FROM skills WHERE name IN ('Docker', 'Kubernetes', 'AWS', 'CI/CD')) s
ON DUPLICATE KEY UPDATE job_id = job_id;

INSERT INTO job_skill (job_id, skill_id)
SELECT j.id, s.id FROM 
    (SELECT id FROM jobs WHERE name = 'Backend Node.js Developer' LIMIT 1) j,
    (SELECT id FROM skills WHERE name IN ('Node.js', 'JavaScript', 'MongoDB', 'Redis')) s
ON DUPLICATE KEY UPDATE job_id = job_id;

INSERT INTO job_skill (job_id, skill_id)
SELECT j.id, s.id FROM 
    (SELECT id FROM jobs WHERE name = '.NET Core Developer' LIMIT 1) j,
    (SELECT id FROM skills WHERE name IN ('C#', '.NET Core', 'MySQL', 'Redis')) s
ON DUPLICATE KEY UPDATE job_id = job_id;

INSERT INTO job_skill (job_id, skill_id)
SELECT j.id, s.id FROM 
    (SELECT id FROM jobs WHERE name = 'Full-stack Developer' LIMIT 1) j,
    (SELECT id FROM skills WHERE name IN ('Angular', 'Spring Boot', 'Java', 'PostgreSQL')) s
ON DUPLICATE KEY UPDATE job_id = job_id;

INSERT INTO job_skill (job_id, skill_id)
SELECT j.id, s.id FROM 
    (SELECT id FROM jobs WHERE name = 'React Native Developer' LIMIT 1) j,
    (SELECT id FROM skills WHERE name IN ('React Native', 'JavaScript', 'TypeScript')) s
ON DUPLICATE KEY UPDATE job_id = job_id;

INSERT INTO job_skill (job_id, skill_id)
SELECT j.id, s.id FROM 
    (SELECT id FROM jobs WHERE name = 'PHP Laravel Developer' LIMIT 1) j,
    (SELECT id FROM skills WHERE name IN ('PHP', 'Laravel', 'MySQL', 'Redis')) s
ON DUPLICATE KEY UPDATE job_id = job_id;

-- =====================================================
-- RESUMES DATA (30 records)
-- Map users to jobs they applied for
-- =====================================================

-- Get some user and job IDs for creating resumes
SET @user1 = (SELECT id FROM users WHERE email = 'nguyenvan@gmail.com' LIMIT 1);
SET @user2 = (SELECT id FROM users WHERE email = 'tranthib@gmail.com' LIMIT 1);
SET @user3 = (SELECT id FROM users WHERE email = 'levanc@gmail.com' LIMIT 1);
SET @user4 = (SELECT id FROM users WHERE email = 'phamthid@gmail.com' LIMIT 1);
SET @user5 = (SELECT id FROM users WHERE email = 'hoangvane@gmail.com' LIMIT 1);
SET @user6 = (SELECT id FROM users WHERE email = 'vothif@gmail.com' LIMIT 1);
SET @user7 = (SELECT id FROM users WHERE email = 'buivang@gmail.com' LIMIT 1);
SET @user8 = (SELECT id FROM users WHERE email = 'dinhthih@gmail.com' LIMIT 1);
SET @user9 = (SELECT id FROM users WHERE email = 'lyvani@gmail.com' LIMIT 1);
SET @user10 = (SELECT id FROM users WHERE email = 'ngothij@gmail.com' LIMIT 1);

SET @job1 = (SELECT id FROM jobs WHERE name = 'Senior Java Developer' LIMIT 1);
SET @job2 = (SELECT id FROM jobs WHERE name = 'React Frontend Developer' LIMIT 1);
SET @job3 = (SELECT id FROM jobs WHERE name = 'Python AI Engineer' LIMIT 1);
SET @job4 = (SELECT id FROM jobs WHERE name = 'DevOps Engineer' LIMIT 1);
SET @job5 = (SELECT id FROM jobs WHERE name = 'Backend Node.js Developer' LIMIT 1);
SET @job6 = (SELECT id FROM jobs WHERE name = 'Full-stack Developer' LIMIT 1);
SET @job7 = (SELECT id FROM jobs WHERE name = 'Junior Java Developer' LIMIT 1);
SET @job8 = (SELECT id FROM jobs WHERE name = 'Frontend Intern' LIMIT 1);

INSERT INTO resumes (email, file_key, status, user_id, job_id, version, created_at, updated_at, created_by, modified_by)
VALUES 
    ('nguyenvan@gmail.com', 'resume_001.pdf', 'PENDING', @user1, @job1, 1, NOW(), NOW(), 'nguyenvan@gmail.com', 'nguyenvan@gmail.com'),
    ('nguyenvan@gmail.com', 'resume_002.pdf', 'REVIEWING', @user1, @job6, 1, NOW(), NOW(), 'nguyenvan@gmail.com', 'nguyenvan@gmail.com'),
    ('tranthib@gmail.com', 'resume_003.pdf', 'PENDING', @user2, @job2, 1, NOW(), NOW(), 'tranthib@gmail.com', 'tranthib@gmail.com'),
    ('tranthib@gmail.com', 'resume_004.pdf', 'APPROVED', @user2, @job8, 1, NOW(), NOW(), 'tranthib@gmail.com', 'tranthib@gmail.com'),
    ('levanc@gmail.com', 'resume_005.pdf', 'PENDING', @user3, @job1, 1, NOW(), NOW(), 'levanc@gmail.com', 'levanc@gmail.com'),
    ('levanc@gmail.com', 'resume_006.pdf', 'REVIEWING', @user3, @job4, 1, NOW(), NOW(), 'levanc@gmail.com', 'levanc@gmail.com'),
    ('phamthid@gmail.com', 'resume_007.pdf', 'REJECTED', @user4, @job3, 1, NOW(), NOW(), 'phamthid@gmail.com', 'phamthid@gmail.com'),
    ('phamthid@gmail.com', 'resume_008.pdf', 'PENDING', @user4, @job2, 1, NOW(), NOW(), 'phamthid@gmail.com', 'phamthid@gmail.com'),
    ('hoangvane@gmail.com', 'resume_009.pdf', 'APPROVED', @user5, @job5, 1, NOW(), NOW(), 'hoangvane@gmail.com', 'hoangvane@gmail.com'),
    ('hoangvane@gmail.com', 'resume_010.pdf', 'PENDING', @user5, @job6, 1, NOW(), NOW(), 'hoangvane@gmail.com', 'hoangvane@gmail.com'),
    ('vothif@gmail.com', 'resume_011.pdf', 'REVIEWING', @user6, @job2, 1, NOW(), NOW(), 'vothif@gmail.com', 'vothif@gmail.com'),
    ('vothif@gmail.com', 'resume_012.pdf', 'PENDING', @user6, @job8, 1, NOW(), NOW(), 'vothif@gmail.com', 'vothif@gmail.com'),
    ('buivang@gmail.com', 'resume_013.pdf', 'APPROVED', @user7, @job7, 1, NOW(), NOW(), 'buivang@gmail.com', 'buivang@gmail.com'),
    ('buivang@gmail.com', 'resume_014.pdf', 'REVIEWING', @user7, @job1, 1, NOW(), NOW(), 'buivang@gmail.com', 'buivang@gmail.com'),
    ('dinhthih@gmail.com', 'resume_015.pdf', 'PENDING', @user8, @job2, 1, NOW(), NOW(), 'dinhthih@gmail.com', 'dinhthih@gmail.com'),
    ('dinhthih@gmail.com', 'resume_016.pdf', 'REJECTED', @user8, @job3, 1, NOW(), NOW(), 'dinhthih@gmail.com', 'dinhthih@gmail.com'),
    ('lyvani@gmail.com', 'resume_017.pdf', 'APPROVED', @user9, @job5, 1, NOW(), NOW(), 'lyvani@gmail.com', 'lyvani@gmail.com'),
    ('lyvani@gmail.com', 'resume_018.pdf', 'PENDING', @user9, @job4, 1, NOW(), NOW(), 'lyvani@gmail.com', 'lyvani@gmail.com'),
    ('ngothij@gmail.com', 'resume_019.pdf', 'REVIEWING', @user10, @job8, 1, NOW(), NOW(), 'ngothij@gmail.com', 'ngothij@gmail.com'),
    ('ngothij@gmail.com', 'resume_020.pdf', 'PENDING', @user10, @job2, 1, NOW(), NOW(), 'ngothij@gmail.com', 'ngothij@gmail.com'),
    ('dangvank@gmail.com', 'resume_021.pdf', 'APPROVED', (SELECT id FROM users WHERE email = 'dangvank@gmail.com'), @job7, 1, NOW(), NOW(), 'dangvank@gmail.com', 'dangvank@gmail.com'),
    ('truongthil@gmail.com', 'resume_022.pdf', 'PENDING', (SELECT id FROM users WHERE email = 'truongthil@gmail.com'), @job8, 1, NOW(), NOW(), 'truongthil@gmail.com', 'truongthil@gmail.com'),
    ('dovanlam@gmail.com', 'resume_023.pdf', 'REVIEWING', (SELECT id FROM users WHERE email = 'dovanlam@gmail.com'), @job1, 1, NOW(), NOW(), 'dovanlam@gmail.com', 'dovanlam@gmail.com'),
    ('hathimai@gmail.com', 'resume_024.pdf', 'REJECTED', (SELECT id FROM users WHERE email = 'hathimai@gmail.com'), @job2, 1, NOW(), NOW(), 'hathimai@gmail.com', 'hathimai@gmail.com'),
    ('vuvannam@gmail.com', 'resume_025.pdf', 'PENDING', (SELECT id FROM users WHERE email = 'vuvannam@gmail.com'), @job5, 1, NOW(), NOW(), 'vuvannam@gmail.com', 'vuvannam@gmail.com'),
    ('maithioanh@gmail.com', 'resume_026.pdf', 'APPROVED', (SELECT id FROM users WHERE email = 'maithioanh@gmail.com'), @job8, 1, NOW(), NOW(), 'maithioanh@gmail.com', 'maithioanh@gmail.com'),
    ('tongvanphuc@gmail.com', 'resume_027.pdf', 'REVIEWING', (SELECT id FROM users WHERE email = 'tongvanphuc@gmail.com'), @job4, 1, NOW(), NOW(), 'tongvanphuc@gmail.com', 'tongvanphuc@gmail.com'),
    ('luuthiquynh@gmail.com', 'resume_028.pdf', 'PENDING', (SELECT id FROM users WHERE email = 'luuthiquynh@gmail.com'), @job2, 1, NOW(), NOW(), 'luuthiquynh@gmail.com', 'luuthiquynh@gmail.com'),
    ('caovanrung@gmail.com', 'resume_029.pdf', 'APPROVED', (SELECT id FROM users WHERE email = 'caovanrung@gmail.com'), @job1, 1, NOW(), NOW(), 'caovanrung@gmail.com', 'caovanrung@gmail.com'),
    ('duongthisan@gmail.com', 'resume_030.pdf', 'PENDING', (SELECT id FROM users WHERE email = 'duongthisan@gmail.com'), @job8, 1, NOW(), NOW(), 'duongthisan@gmail.com', 'duongthisan@gmail.com')
ON DUPLICATE KEY UPDATE
    updated_at = NOW();

-- =====================================================
-- SUMMARY:
-- Skills: 25 records
-- Companies: 20 records  
-- Users: 30 records (10 recruiters + 20 job seekers)
-- Jobs: 25 records
-- Resumes: 30 records
-- Total: 130+ records
-- =====================================================

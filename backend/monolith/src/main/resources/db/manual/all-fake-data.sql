-- =====================================================
-- FAKE DATA FOR TALENTBRIDGE - PART 1
-- Skills & Companies
-- =====================================================

-- =====================================================
-- 1. SKILLS DATA (25 records)
-- =====================================================
INSERT INTO skills (name, created_at, updated_at, created_by, modified_by)
VALUES 
    ('Java', NOW(), NOW(), 'system', 'system'),
    ('Spring Boot', NOW(), NOW(), 'system', 'system'),
    ('React', NOW(), NOW(), 'system', 'system'),
    ('Angular', NOW(), NOW(), 'system', 'system'),
    ('Vue.js', NOW(), NOW(), 'system', 'system'),
    ('Node.js', NOW(), NOW(), 'system', 'system'),
    ('Python', NOW(), NOW(), 'system', 'system'),
    ('Django', NOW(), NOW(), 'system', 'system'),
    ('PHP', NOW(), NOW(), 'system', 'system'),
    ('Laravel', NOW(), NOW(), 'system', 'system'),
    ('MySQL', NOW(), NOW(), 'system', 'system'),
    ('PostgreSQL', NOW(), NOW(), 'system', 'system'),
    ('MongoDB', NOW(), NOW(), 'system', 'system'),
    ('Docker', NOW(), NOW(), 'system', 'system'),
    ('Kubernetes', NOW(), NOW(), 'system', 'system'),
    ('AWS', NOW(), NOW(), 'system', 'system'),
    ('TypeScript', NOW(), NOW(), 'system', 'system'),
    ('JavaScript', NOW(), NOW(), 'system', 'system'),
    ('C#', NOW(), NOW(), 'system', 'system'),
    ('.NET Core', NOW(), NOW(), 'system', 'system'),
    ('Flutter', NOW(), NOW(), 'system', 'system'),
    ('React Native', NOW(), NOW(), 'system', 'system'),
    ('Redis', NOW(), NOW(), 'system', 'system'),
    ('GraphQL', NOW(), NOW(), 'system', 'system'),
    ('CI/CD', NOW(), NOW(), 'system', 'system')
ON DUPLICATE KEY UPDATE
    updated_at = NOW();

-- =====================================================
-- 2. COMPANIES DATA (20 records)
-- =====================================================
INSERT INTO companies (name, description, address, created_at, updated_at, created_by, modified_by)
VALUES 
    ('FPT Software', 'Vietnam''s leading software company, providing IT services worldwide with more than 30,000 employees', 'Lot F8, Saigon Hi-Tech Park, District 9, Ho Chi Minh City', NOW(), NOW(), 'system', 'system'),
    ('VNG Corporation', 'Leading technology company behind Zalo, ZaloPay and Zing, serving 100+ million users', 'Z06, Street 13, Tân Thuận Đông, District 7, Ho Chi Minh City', NOW(), NOW(), 'system', 'system'),
    ('Viettel Software', 'Software development centre of the Viettel Group', 'Viettel Building, 285 Cách Mạng Tháng 8, District 10, Ho Chi Minh City', NOW(), NOW(), 'system', 'system'),
    ('TMA Solutions', 'Software company with 4,000+ engineers and 25+ years of experience serving global markets', '111 Nguyễn Đình Chiểu, District 3, Ho Chi Minh City', NOW(), NOW(), 'system', 'system'),
    ('KMS Technology', 'Software development and technology services for US clients', '123 Cộng Hòa, Tân Bình, Ho Chi Minh City', NOW(), NOW(), 'system', 'system'),
    ('NashTech Vietnam', 'Global technology company of the Harvey Nash Group with 2,000+ employees in Vietnam', '364 Cộng Hòa, Tân Bình, Ho Chi Minh City', NOW(), NOW(), 'system', 'system'),
    ('Grab Vietnam', 'Southeast Asia''s leading super-app for ride-hailing, food delivery and payments', 'Mapletree Business Centre, Q.7, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('Shopee Vietnam', 'The region''s leading e-commerce platform with 50+ million users', 'Floors 29-31, Viettel Tower, District 10, Ho Chi Minh City', NOW(), NOW(), 'system', 'system'),
    ('Tiki Corporation', 'Home-grown Vietnamese marketplace with huge warehouses and 2-hour delivery', '52 Út Tịch, Tân Bình, Ho Chi Minh City', NOW(), NOW(), 'system', 'system'),
    ('MoMo', 'Vietnam''s number one e-wallet with 31+ million users', 'Phú Mỹ Hưng Tower, District 7, Ho Chi Minh City', NOW(), NOW(), 'system', 'system'),
    ('VinAI', 'Vietnam''s leading AI research institute, part of VinGroup', 'Vinhomes Grand Park, Q.9, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('Axon Active Vietnam', 'Swiss software company with 500+ engineers in Vietnam', '7-9 Tân Cảng, Bình Thạnh, Ho Chi Minh City', NOW(), NOW(), 'system', 'system'),
    ('Samsung SDS Vietnam', 'Samsung''s software development branch in Vietnam', 'Saigon Trade Center, Q.1, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('Bosch Vietnam', 'German technology group with an R&D centre in Vietnam', '10/14 Cách Mạng Tháng 8, District 12, Ho Chi Minh City', NOW(), NOW(), 'system', 'system'),
    ('Home Credit Vietnam', 'Leading fintech company for consumer lending', 'Phú Mỹ Hưng Building, District 7, Ho Chi Minh City', NOW(), NOW(), 'system', 'system'),
    ('VPBank Technology', 'VPBank''s digital technology centre', '89 Láng Hạ, Đống Đa, Hanoi', NOW(), NOW(), 'system', 'system'),
    ('Techcombank Digital', 'Techcombank''s digital banking division', '191 Bà Triệu, Hai Bà Trưng, Hanoi', NOW(), NOW(), 'system', 'system'),
    ('Lazada Tech Hub', 'Lazada''s technology centre in Vietnam', 'Saigon Centre, Q.1, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('Sky Mavis', 'Blockchain game company behind Axie Infinity', '6 Thái Văn Lung, District 1, Ho Chi Minh City', NOW(), NOW(), 'system', 'system'),
    ('NFQ Asia', 'German technology company specialising in e-commerce and fintech', '13 Paster, Q.1, TP.HCM', NOW(), NOW(), 'system', 'system')
ON DUPLICATE KEY UPDATE
    updated_at = NOW();
-- =====================================================
-- FAKE DATA FOR TALENTBRIDGE - PART 2
-- Users (30 records)
-- =====================================================

-- Get role IDs
SET @admin_role = (SELECT id FROM roles WHERE name = 'ADMIN' LIMIT 1);
SET @recruiter_role = (SELECT id FROM roles WHERE name = 'RECRUITER' LIMIT 1);
SET @user_role = (SELECT id FROM roles WHERE name = 'USER' LIMIT 1);

-- Password for all: password123 ($2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy)

-- Recruiter users (10 records)
INSERT INTO users (email, name, password, dob, address, gender, role_id, company_id, created_at, updated_at, created_by, modified_by)
VALUES 
    ('hr.fpt@fpt.com.vn', 'Lê Thị Mai', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1990-03-12', 'District 9, Ho Chi Minh City', 'FEMALE', @recruiter_role, 1, NOW(), NOW(), 'system', 'system'),
    ('recruiter@vng.com.vn', 'Phạm Văn Hùng', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1987-07-25', 'District 7, Ho Chi Minh City', 'MALE', @recruiter_role, 2, NOW(), NOW(), 'system', 'system'),
    ('hr@viettel.com.vn', 'Nguyễn Thị Hương', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1992-11-30', 'Ho Chi Minh City', 'FEMALE', @recruiter_role, 3, NOW(), NOW(), 'system', 'system'),
    ('talent@tma.com.vn', 'Trần Văn Nam', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1989-04-18', 'District 3, Ho Chi Minh City', 'MALE', @recruiter_role, 4, NOW(), NOW(), 'system', 'system'),
    ('hr@kms-technology.com', 'Võ Thị Lan', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1991-09-05', 'Tân Bình, Ho Chi Minh City', 'FEMALE', @recruiter_role, 5, NOW(), NOW(), 'system', 'system'),
    ('recruiter@grab.com', 'Đinh Thị Thu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1993-02-14', 'District 7, Ho Chi Minh City', 'FEMALE', @recruiter_role, 7, NOW(), NOW(), 'system', 'system'),
    ('talent@shopee.vn', 'Lý Văn Tùng', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1990-06-08', 'District 10, Ho Chi Minh City', 'MALE', @recruiter_role, 8, NOW(), NOW(), 'system', 'system'),
    ('hr@tiki.vn', 'Hoàng Thị Nhung', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1991-10-16', 'Tân Bình, Ho Chi Minh City', 'FEMALE', @recruiter_role, 9, NOW(), NOW(), 'system', 'system'),
    ('hr@momo.vn', 'Phan Thị Hạnh', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1992-05-19', 'District 7, Ho Chi Minh City', 'FEMALE', @recruiter_role, 10, NOW(), NOW(), 'system', 'system'),
    ('talent@vinai.io', 'Vũ Văn Thành', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1987-08-11', 'Ho Chi Minh City', 'MALE', @recruiter_role, 11, NOW(), NOW(), 'system', 'system')
ON DUPLICATE KEY UPDATE
    updated_at = NOW();

-- Regular users / Job seekers (20 records)
INSERT INTO users (email, name, password, dob, address, gender, role_id, created_at, updated_at, created_by, modified_by)
VALUES 
    ('nguyenvan@gmail.com', 'Nguyễn Văn An', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1995-03-15', 'Gò Vấp District, Ho Chi Minh City', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('tranthib@gmail.com', 'Trần Thị Bình', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1996-07-22', 'Bình Tân District, Ho Chi Minh City', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('levanc@gmail.com', 'Lê Văn Cường', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1994-11-08', 'Thủ Đức District, Ho Chi Minh City', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('phamthid@gmail.com', 'Phạm Thị Dung', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1997-02-14', 'District 12, Ho Chi Minh City', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('hoangvane@gmail.com', 'Hoàng Văn Em', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1993-06-30', 'Tân Phú District, Ho Chi Minh City', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('vothif@gmail.com', 'Võ Thị Phương', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1998-09-18', 'Phú Nhuận District, Ho Chi Minh City', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('buivang@gmail.com', 'Bùi Văn Giang', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1995-12-05', 'Bình Thạnh District, Ho Chi Minh City', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('dinhthih@gmail.com', 'Đinh Thị Hoa', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1996-04-25', 'District 2, Ho Chi Minh City', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('lyvani@gmail.com', 'Lý Văn Inh', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1994-08-12', 'District 4, Ho Chi Minh City', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('ngothij@gmail.com', 'Ngô Thị Kiều', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1997-10-28', 'District 5, Ho Chi Minh City', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('dangvank@gmail.com', 'Đặng Văn Kiên', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1995-01-16', 'District 6, Ho Chi Minh City', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('truongthil@gmail.com', 'Trương Thị Liên', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1996-05-07', 'District 8, Ho Chi Minh City', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('dovanlam@gmail.com', 'Đỗ Văn Lâm', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1993-09-23', 'District 11, Ho Chi Minh City', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('hathimai@gmail.com', 'Hà Thị Mai', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1998-12-11', 'Hóc Môn, Ho Chi Minh City', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('vuvannam@gmail.com', 'Vũ Văn Nam', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1994-03-29', 'Củ Chi, Ho Chi Minh City', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('maithioanh@gmail.com', 'Mai Thị Oanh', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1997-06-17', 'Nhà Bè, Ho Chi Minh City', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('tongvanphuc@gmail.com', 'Tống Văn Phúc', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1995-08-04', 'Cần Giờ, Ho Chi Minh City', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('luuthiquynh@gmail.com', 'Lưu Thị Quỳnh', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1996-11-20', 'Bình Chánh, Ho Chi Minh City', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('caovanrung@gmail.com', 'Cao Văn Rừng', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1994-02-08', 'District 1, Ho Chi Minh City', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('duongthisan@gmail.com', 'Dương Thị San', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1997-04-26', 'District 3, Ho Chi Minh City', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system')
ON DUPLICATE KEY UPDATE
    updated_at = NOW();
-- =====================================================
-- FAKE DATA FOR TALENTBRIDGE - PART 3
-- Jobs (25 records)
-- =====================================================

INSERT INTO jobs (name, location, salary, quantity, level, description, start_date, end_date, active, company_id, created_at, updated_at, created_by, modified_by)
VALUES 
    -- FPT Software jobs
    ('Senior Java Developer', 'District 9, Ho Chi Minh City', 25000000, 3, 'SENIOR', 'Build Java/Spring Boot applications for Japanese clients. Requires 5+ years of Java, Spring Framework and microservices. Japanese N3 is a plus.', DATE_ADD(NOW(), INTERVAL -10 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), true, 1, NOW(), NOW(), 'system', 'system'),
    ('React Frontend Developer', 'District 9, Ho Chi Minh City', 18000000, 5, 'MIDDLE', 'Build UIs with React, Redux and TypeScript. Requires 3+ years of experience and fluency in HTML5, CSS3 and responsive design.', DATE_ADD(NOW(), INTERVAL -5 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY), true, 1, NOW(), NOW(), 'system', 'system'),
    
    -- VNG Corporation jobs  
    ('Python AI Engineer', 'District 7, Ho Chi Minh City', 30000000, 2, 'SENIOR', 'Build AI/ML systems for Zalo. Requires Python, TensorFlow and PyTorch experience. NLP or computer vision experience preferred.', DATE_ADD(NOW(), INTERVAL -7 DAY), DATE_ADD(NOW(), INTERVAL 45 DAY), true, 2, NOW(), NOW(), 'system', 'system'),
    ('DevOps Engineer', 'District 7, Ho Chi Minh City', 22000000, 3, 'MIDDLE', 'Run AWS/GCP cloud infrastructure and CI/CD pipelines. Requires Docker, Kubernetes and Jenkins; 3+ years of experience.', DATE_ADD(NOW(), INTERVAL -3 DAY), DATE_ADD(NOW(), INTERVAL 40 DAY), true, 2, NOW(), NOW(), 'system', 'system'),
    
    -- Viettel Software jobs
    ('Backend Node.js Developer', 'District 10, Ho Chi Minh City', 20000000, 4, 'MIDDLE', 'Build backends with Node.js, Express and MongoDB. Requires 2+ years of experience and a good grasp of RESTful APIs and microservices.', DATE_ADD(NOW(), INTERVAL -8 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY), true, 3, NOW(), NOW(), 'system', 'system'),
    ('.NET Core Developer', 'District 10, Ho Chi Minh City', 23000000, 2, 'SENIOR', 'Build enterprise systems with .NET Core and SQL Server. Requires 4+ years of experience, fluent C# and Entity Framework.', DATE_ADD(NOW(), INTERVAL -12 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), true, 3, NOW(), NOW(), 'system', 'system'),
    
    -- TMA Solutions jobs
    ('Full-stack Developer', 'District 3, Ho Chi Minh City', 22000000, 6, 'MIDDLE', 'Full-stack development with Angular and Spring Boot. Requires 3+ years of experience and good spoken English.', DATE_ADD(NOW(), INTERVAL -6 DAY), DATE_ADD(NOW(), INTERVAL 42 DAY), true, 4, NOW(), NOW(), 'system', 'system'),
    ('QA Automation Engineer', 'District 3, Ho Chi Minh City', 18000000, 3, 'MIDDLE', 'Write automated tests with Selenium and Cypress. Requires 2+ years of test automation and Java or Python.', DATE_ADD(NOW(), INTERVAL -4 DAY), DATE_ADD(NOW(), INTERVAL 38 DAY), true, 4, NOW(), NOW(), 'system', 'system'),
    
    -- KMS Technology jobs
    ('React Native Developer', 'Tân Bình, Ho Chi Minh City', 21000000, 4, 'MIDDLE', 'Build React Native mobile apps for US clients. Requires 3+ years of experience and apps published to the stores.', DATE_ADD(NOW(), INTERVAL -9 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY), true, 5, NOW(), NOW(), 'system', 'system'),
    ('AWS Cloud Architect', 'Tân Bình, Ho Chi Minh City', 35000000, 1, 'SENIOR', 'Design cloud architectures on AWS. Requires 5+ years of experience and the AWS Solutions Architect certification.', DATE_ADD(NOW(), INTERVAL -15 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), true, 5, NOW(), NOW(), 'system', 'system'),
    
    -- Grab Vietnam jobs
    ('Senior Backend Engineer', 'District 7, Ho Chi Minh City', 32000000, 3, 'SENIOR', 'Build backend microservices in Go/Java. Requires 5+ years of experience with high-traffic systems.', DATE_ADD(NOW(), INTERVAL -5 DAY), DATE_ADD(NOW(), INTERVAL 45 DAY), true, 7, NOW(), NOW(), 'system', 'system'),
    ('Data Engineer', 'District 7, Ho Chi Minh City', 28000000, 2, 'SENIOR', 'Build data pipelines with Spark and Kafka. Requires 4+ years of big data experience and fluent Python/Scala.', DATE_ADD(NOW(), INTERVAL -7 DAY), DATE_ADD(NOW(), INTERVAL 40 DAY), true, 7, NOW(), NOW(), 'system', 'system'),
    
    -- Shopee Vietnam jobs
    ('Frontend Vue.js Developer', 'District 10, Ho Chi Minh City', 19000000, 5, 'MIDDLE', 'Build e-commerce frontends with Vue.js. Requires 2+ years of experience and an understanding of performance optimisation.', DATE_ADD(NOW(), INTERVAL -3 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY), true, 8, NOW(), NOW(), 'system', 'system'),
    ('Machine Learning Engineer', 'District 10, Ho Chi Minh City', 30000000, 2, 'SENIOR', 'Build recommendation systems. Requires 4+ years of ML experience and fluent Python, TensorFlow/PyTorch.', DATE_ADD(NOW(), INTERVAL -10 DAY), DATE_ADD(NOW(), INTERVAL 45 DAY), true, 8, NOW(), NOW(), 'system', 'system'),
    
    -- Tiki jobs
    ('PHP Laravel Developer', 'Tân Bình, Ho Chi Minh City', 17000000, 4, 'MIDDLE', 'Build e-commerce backends with Laravel. Requires 2+ years of PHP, MySQL and Redis.', DATE_ADD(NOW(), INTERVAL -8 DAY), DATE_ADD(NOW(), INTERVAL 32 DAY), true, 9, NOW(), NOW(), 'system', 'system'),
    ('Android Developer', 'Tân Bình, Ho Chi Minh City', 20000000, 3, 'MIDDLE', 'Build Android apps with Kotlin. Requires 3+ years of experience and knowledge of Android Architecture Components.', DATE_ADD(NOW(), INTERVAL -6 DAY), DATE_ADD(NOW(), INTERVAL 38 DAY), true, 9, NOW(), NOW(), 'system', 'system'),
    
    -- MoMo jobs
    ('iOS Developer', 'District 7, Ho Chi Minh City', 22000000, 3, 'MIDDLE', 'Build iOS apps with Swift. Requires 3+ years of experience and fluent UIKit and SwiftUI.', DATE_ADD(NOW(), INTERVAL -4 DAY), DATE_ADD(NOW(), INTERVAL 40 DAY), true, 10, NOW(), NOW(), 'system', 'system'),
    ('Security Engineer', 'District 7, Ho Chi Minh City', 28000000, 2, 'SENIOR', 'Secure fintech systems. Requires 4+ years of security experience, OWASP and penetration testing.', DATE_ADD(NOW(), INTERVAL -12 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY), true, 10, NOW(), NOW(), 'system', 'system'),
    
    -- VinAI jobs
    ('Computer Vision Engineer', 'District 9, Ho Chi Minh City', 35000000, 2, 'SENIOR', 'Computer vision research and development. Requires a Master''s or PhD and experience with OpenCV and deep learning.', DATE_ADD(NOW(), INTERVAL -7 DAY), DATE_ADD(NOW(), INTERVAL 50 DAY), true, 11, NOW(), NOW(), 'system', 'system'),
    ('Research Scientist', 'District 9, Ho Chi Minh City', 40000000, 1, 'SENIOR', 'AI/ML research. Requires a PhD or 5+ years of research experience with publications at international conferences.', DATE_ADD(NOW(), INTERVAL -14 DAY), DATE_ADD(NOW(), INTERVAL 60 DAY), true, 11, NOW(), NOW(), 'system', 'system'),
    
    -- Entry level positions
    ('Junior Java Developer', 'Bình Thạnh, Ho Chi Minh City', 10000000, 8, 'FRESHER', 'Fresher/junior Java developer. Requires an IT degree, OOP and core Java. Two months of training provided.', DATE_ADD(NOW(), INTERVAL -2 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), true, 12, NOW(), NOW(), 'system', 'system'),
    ('Frontend Intern', 'District 1, Ho Chi Minh City', 5000000, 10, 'INTERN', '3-6 month frontend internship for 3rd-4th year students with basic HTML/CSS/JavaScript.', DATE_ADD(NOW(), INTERVAL -1 DAY), DATE_ADD(NOW(), INTERVAL 45 DAY), true, 13, NOW(), NOW(), 'system', 'system'),
    ('Junior QA Tester', 'District 12, Ho Chi Minh City', 8000000, 5, 'FRESHER', 'Fresher manual tester. Requires an IT or related degree, a testing mindset and reading English.', DATE_ADD(NOW(), INTERVAL -3 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY), true, 14, NOW(), NOW(), 'system', 'system'),
    ('Junior PHP Developer', 'District 7, Ho Chi Minh City', 9000000, 6, 'FRESHER', 'Fresher PHP developer. Requires basic PHP and MySQL; personal projects are a plus.', DATE_ADD(NOW(), INTERVAL -5 DAY), DATE_ADD(NOW(), INTERVAL 40 DAY), true, 15, NOW(), NOW(), 'system', 'system'),
    ('Business Analyst Fresher', 'District 1, Ho Chi Minh City', 10000000, 4, 'FRESHER', 'Fresher business analyst for fintech projects. Requires good English, logical thinking and communication skills.', DATE_ADD(NOW(), INTERVAL -4 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), true, 18, NOW(), NOW(), 'system', 'system')
ON DUPLICATE KEY UPDATE
    updated_at = NOW();
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

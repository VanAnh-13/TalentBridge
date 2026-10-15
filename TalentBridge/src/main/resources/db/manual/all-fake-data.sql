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
    ('FPT Software', 'Công ty phần mềm hàng đầu Việt Nam, cung cấp dịch vụ IT toàn cầu với hơn 30,000 nhân viên', 'Lô F8, Khu Công nghệ cao, Q.9, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('VNG Corporation', 'Công ty công nghệ hàng đầu với các sản phẩm Zalo, ZaloPay, Zing phục vụ 100+ triệu người dùng', 'Z06, Đường số 13, Tân Thuận Đông, Q.7, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('Viettel Software', 'Trung tâm phát triển phần mềm thuộc Tập đoàn Quân đội Viettel', 'Tòa nhà Viettel, 285 Cách Mạng Tháng 8, Q.10, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('TMA Solutions', 'Công ty phần mềm với hơn 4000 kỹ sư, 25+ năm kinh nghiệm phục vụ thị trường toàn cầu', '111 Nguyễn Đình Chiểu, Q.3, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('KMS Technology', 'Công ty phát triển phần mềm và dịch vụ công nghệ cho khách hàng Mỹ', '123 Cộng Hòa, Tân Bình, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('NashTech Vietnam', 'Công ty công nghệ toàn cầu của Harvey Nash Group với 2000+ nhân viên tại VN', '364 Cộng Hòa, Tân Bình, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('Grab Vietnam', 'Siêu ứng dụng hàng đầu Đông Nam Á về gọi xe, giao đồ ăn và thanh toán', 'Mapletree Business Centre, Q.7, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('Shopee Vietnam', 'Nền tảng thương mại điện tử hàng đầu khu vực với 50+ triệu người dùng', 'Lầu 29-31, Viettel Tower, Q.10, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('Tiki Corporation', 'Sàn TMĐT thuần Việt với kho hàng khổng lồ và giao hàng nhanh 2h', '52 Út Tịch, Q.Tân Bình, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('MoMo', 'Ví điện tử số 1 Việt Nam với 31+ triệu người dùng', 'Tòa nhà Phú Mỹ Hưng Tower, Q.7, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('VinAI', 'Viện nghiên cứu AI hàng đầu VN thuộc VinGroup', 'Vinhomes Grand Park, Q.9, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('Axon Active Vietnam', 'Công ty phát triển phần mềm Thụy Sĩ với 500+ kỹ sư tại VN', '7-9 Tân Cảng, Bình Thạnh, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('Samsung SDS Vietnam', 'Chi nhánh phát triển phần mềm của Samsung tại VN', 'Saigon Trade Center, Q.1, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('Bosch Vietnam', 'Tập đoàn công nghệ Đức với trung tâm R&D tại VN', '10/14 Cách Mạng Tháng 8, Q.12, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('Home Credit Vietnam', 'Công ty fintech hàng đầu về cho vay tiêu dùng', 'Tòa nhà Phú Mỹ Hưng, Q.7, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('VPBank Technology', 'Trung tâm công nghệ số của VPBank', '89 Láng Hạ, Đống Đa, Hà Nội', NOW(), NOW(), 'system', 'system'),
    ('Techcombank Digital', 'Khối số hóa ngân hàng Techcombank', '191 Bà Triệu, Hai Bà Trưng, Hà Nội', NOW(), NOW(), 'system', 'system'),
    ('Lazada Tech Hub', 'Trung tâm công nghệ của Lazada tại VN', 'Saigon Centre, Q.1, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('Sky Mavis', 'Công ty game blockchain phát triển Axie Infinity', '6 Thái Văn Lung, Q.1, TP.HCM', NOW(), NOW(), 'system', 'system'),
    ('NFQ Asia', 'Công ty công nghệ Đức chuyên về e-commerce và fintech', '13 Paster, Q.1, TP.HCM', NOW(), NOW(), 'system', 'system')
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
    ('hr.fpt@fpt.com.vn', 'Lê Thị Mai', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1990-03-12', 'Quận 9, TP.HCM', 'FEMALE', @recruiter_role, 1, NOW(), NOW(), 'system', 'system'),
    ('recruiter@vng.com.vn', 'Phạm Văn Hùng', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1987-07-25', 'Quận 7, TP.HCM', 'MALE', @recruiter_role, 2, NOW(), NOW(), 'system', 'system'),
    ('hr@viettel.com.vn', 'Nguyễn Thị Hương', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1992-11-30', 'TP.HCM', 'FEMALE', @recruiter_role, 3, NOW(), NOW(), 'system', 'system'),
    ('talent@tma.com.vn', 'Trần Văn Nam', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1989-04-18', 'Quận 3, TP.HCM', 'MALE', @recruiter_role, 4, NOW(), NOW(), 'system', 'system'),
    ('hr@kms-technology.com', 'Võ Thị Lan', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1991-09-05', 'Tân Bình, TP.HCM', 'FEMALE', @recruiter_role, 5, NOW(), NOW(), 'system', 'system'),
    ('recruiter@grab.com', 'Đinh Thị Thu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1993-02-14', 'Quận 7, TP.HCM', 'FEMALE', @recruiter_role, 7, NOW(), NOW(), 'system', 'system'),
    ('talent@shopee.vn', 'Lý Văn Tùng', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1990-06-08', 'Quận 10, TP.HCM', 'MALE', @recruiter_role, 8, NOW(), NOW(), 'system', 'system'),
    ('hr@tiki.vn', 'Hoàng Thị Nhung', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1991-10-16', 'Tân Bình, TP.HCM', 'FEMALE', @recruiter_role, 9, NOW(), NOW(), 'system', 'system'),
    ('hr@momo.vn', 'Phan Thị Hạnh', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1992-05-19', 'Quận 7, TP.HCM', 'FEMALE', @recruiter_role, 10, NOW(), NOW(), 'system', 'system'),
    ('talent@vinai.io', 'Vũ Văn Thành', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1987-08-11', 'TP.HCM', 'MALE', @recruiter_role, 11, NOW(), NOW(), 'system', 'system')
ON DUPLICATE KEY UPDATE
    updated_at = NOW();

-- Regular users / Job seekers (20 records)
INSERT INTO users (email, name, password, dob, address, gender, role_id, created_at, updated_at, created_by, modified_by)
VALUES 
    ('nguyenvan@gmail.com', 'Nguyễn Văn An', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1995-03-15', 'Quận Gò Vấp, TP.HCM', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('tranthib@gmail.com', 'Trần Thị Bình', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1996-07-22', 'Quận Bình Tân, TP.HCM', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('levanc@gmail.com', 'Lê Văn Cường', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1994-11-08', 'Quận Thủ Đức, TP.HCM', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('phamthid@gmail.com', 'Phạm Thị Dung', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1997-02-14', 'Quận 12, TP.HCM', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('hoangvane@gmail.com', 'Hoàng Văn Em', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1993-06-30', 'Quận Tân Phú, TP.HCM', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('vothif@gmail.com', 'Võ Thị Phương', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1998-09-18', 'Quận Phú Nhuận, TP.HCM', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('buivang@gmail.com', 'Bùi Văn Giang', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1995-12-05', 'Quận Bình Thạnh, TP.HCM', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('dinhthih@gmail.com', 'Đinh Thị Hoa', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1996-04-25', 'Quận 2, TP.HCM', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('lyvani@gmail.com', 'Lý Văn Inh', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1994-08-12', 'Quận 4, TP.HCM', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('ngothij@gmail.com', 'Ngô Thị Kiều', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1997-10-28', 'Quận 5, TP.HCM', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('dangvank@gmail.com', 'Đặng Văn Kiên', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1995-01-16', 'Quận 6, TP.HCM', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('truongthil@gmail.com', 'Trương Thị Liên', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1996-05-07', 'Quận 8, TP.HCM', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('dovanlam@gmail.com', 'Đỗ Văn Lâm', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1993-09-23', 'Quận 11, TP.HCM', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('hathimai@gmail.com', 'Hà Thị Mai', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1998-12-11', 'Hóc Môn, TP.HCM', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('vuvannam@gmail.com', 'Vũ Văn Nam', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1994-03-29', 'Củ Chi, TP.HCM', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('maithioanh@gmail.com', 'Mai Thị Oanh', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1997-06-17', 'Nhà Bè, TP.HCM', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('tongvanphuc@gmail.com', 'Tống Văn Phúc', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1995-08-04', 'Cần Giờ, TP.HCM', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('luuthiquynh@gmail.com', 'Lưu Thị Quỳnh', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1996-11-20', 'Bình Chánh, TP.HCM', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('caovanrung@gmail.com', 'Cao Văn Rừng', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1994-02-08', 'Quận 1, TP.HCM', 'MALE', @user_role, NOW(), NOW(), 'system', 'system'),
    ('duongthisan@gmail.com', 'Dương Thị San', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', '1997-04-26', 'Quận 3, TP.HCM', 'FEMALE', @user_role, NOW(), NOW(), 'system', 'system')
ON DUPLICATE KEY UPDATE
    updated_at = NOW();
-- =====================================================
-- FAKE DATA FOR TALENTBRIDGE - PART 3
-- Jobs (25 records)
-- =====================================================

INSERT INTO jobs (name, location, salary, quantity, level, description, start_date, end_date, active, company_id, created_at, updated_at, created_by, modified_by)
VALUES 
    -- FPT Software jobs
    ('Senior Java Developer', 'Quận 9, TP.HCM', 25000000, 3, 'SENIOR', 'Phát triển ứng dụng Java/Spring Boot cho khách hàng Nhật Bản. Yêu cầu 5+ năm kinh nghiệm Java, Spring Framework, Microservices. Tiếng Nhật N3 là lợi thế.', DATE_ADD(NOW(), INTERVAL -10 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), true, 1, NOW(), NOW(), 'system', 'system'),
    ('React Frontend Developer', 'Quận 9, TP.HCM', 18000000, 5, 'MIDDLE', 'Xây dựng UI/UX với React, Redux, TypeScript. Yêu cầu 3+ năm kinh nghiệm. Thành thạo HTML5, CSS3, responsive design.', DATE_ADD(NOW(), INTERVAL -5 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY), true, 1, NOW(), NOW(), 'system', 'system'),
    
    -- VNG Corporation jobs  
    ('Python AI Engineer', 'Quận 7, TP.HCM', 30000000, 2, 'SENIOR', 'Phát triển hệ thống AI/ML cho Zalo. Yêu cầu kinh nghiệm Python, TensorFlow, PyTorch. Ưu tiên có kinh nghiệm NLP, Computer Vision.', DATE_ADD(NOW(), INTERVAL -7 DAY), DATE_ADD(NOW(), INTERVAL 45 DAY), true, 2, NOW(), NOW(), 'system', 'system'),
    ('DevOps Engineer', 'Quận 7, TP.HCM', 22000000, 3, 'MIDDLE', 'Quản lý hạ tầng cloud AWS/GCP, CI/CD pipeline. Yêu cầu thành thạo Docker, Kubernetes, Jenkins. Kinh nghiệm 3+ năm.', DATE_ADD(NOW(), INTERVAL -3 DAY), DATE_ADD(NOW(), INTERVAL 40 DAY), true, 2, NOW(), NOW(), 'system', 'system'),
    
    -- Viettel Software jobs
    ('Backend Node.js Developer', 'Quận 10, TP.HCM', 20000000, 4, 'MIDDLE', 'Phát triển backend với Node.js, Express, MongoDB. Yêu cầu 2+ năm kinh nghiệm, hiểu về RESTful API và microservices.', DATE_ADD(NOW(), INTERVAL -8 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY), true, 3, NOW(), NOW(), 'system', 'system'),
    ('.NET Core Developer', 'Quận 10, TP.HCM', 23000000, 2, 'SENIOR', 'Xây dựng hệ thống enterprise với .NET Core, SQL Server. Yêu cầu 4+ năm kinh nghiệm, thành thạo C#, Entity Framework.', DATE_ADD(NOW(), INTERVAL -12 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), true, 3, NOW(), NOW(), 'system', 'system'),
    
    -- TMA Solutions jobs
    ('Full-stack Developer', 'Quận 3, TP.HCM', 22000000, 6, 'MIDDLE', 'Phát triển full-stack với Angular và Spring Boot. Yêu cầu 3+ năm kinh nghiệm, tiếng Anh giao tiếp tốt.', DATE_ADD(NOW(), INTERVAL -6 DAY), DATE_ADD(NOW(), INTERVAL 42 DAY), true, 4, NOW(), NOW(), 'system', 'system'),
    ('QA Automation Engineer', 'Quận 3, TP.HCM', 18000000, 3, 'MIDDLE', 'Viết test automation với Selenium, Cypress. Yêu cầu 2+ năm kinh nghiệm test automation, biết Java hoặc Python.', DATE_ADD(NOW(), INTERVAL -4 DAY), DATE_ADD(NOW(), INTERVAL 38 DAY), true, 4, NOW(), NOW(), 'system', 'system'),
    
    -- KMS Technology jobs
    ('React Native Developer', 'Tân Bình, TP.HCM', 21000000, 4, 'MIDDLE', 'Phát triển mobile app với React Native cho khách hàng Mỹ. Yêu cầu 3+ năm kinh nghiệm, đã publish app lên store.', DATE_ADD(NOW(), INTERVAL -9 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY), true, 5, NOW(), NOW(), 'system', 'system'),
    ('AWS Cloud Architect', 'Tân Bình, TP.HCM', 35000000, 1, 'SENIOR', 'Thiết kế kiến trúc cloud trên AWS. Yêu cầu 5+ năm kinh nghiệm, có chứng chỉ AWS Solutions Architect.', DATE_ADD(NOW(), INTERVAL -15 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), true, 5, NOW(), NOW(), 'system', 'system'),
    
    -- Grab Vietnam jobs
    ('Senior Backend Engineer', 'Quận 7, TP.HCM', 32000000, 3, 'SENIOR', 'Xây dựng backend microservices với Go/Java. Yêu cầu 5+ năm kinh nghiệm, xử lý high traffic systems.', DATE_ADD(NOW(), INTERVAL -5 DAY), DATE_ADD(NOW(), INTERVAL 45 DAY), true, 7, NOW(), NOW(), 'system', 'system'),
    ('Data Engineer', 'Quận 7, TP.HCM', 28000000, 2, 'SENIOR', 'Xây dựng data pipeline với Spark, Kafka. Yêu cầu 4+ năm kinh nghiệm big data, thành thạo Python/Scala.', DATE_ADD(NOW(), INTERVAL -7 DAY), DATE_ADD(NOW(), INTERVAL 40 DAY), true, 7, NOW(), NOW(), 'system', 'system'),
    
    -- Shopee Vietnam jobs
    ('Frontend Vue.js Developer', 'Quận 10, TP.HCM', 19000000, 5, 'MIDDLE', 'Phát triển frontend e-commerce với Vue.js. Yêu cầu 2+ năm kinh nghiệm, hiểu về performance optimization.', DATE_ADD(NOW(), INTERVAL -3 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY), true, 8, NOW(), NOW(), 'system', 'system'),
    ('Machine Learning Engineer', 'Quận 10, TP.HCM', 30000000, 2, 'SENIOR', 'Phát triển recommendation system. Yêu cầu 4+ năm ML experience, thành thạo Python, TensorFlow/PyTorch.', DATE_ADD(NOW(), INTERVAL -10 DAY), DATE_ADD(NOW(), INTERVAL 45 DAY), true, 8, NOW(), NOW(), 'system', 'system'),
    
    -- Tiki jobs
    ('PHP Laravel Developer', 'Tân Bình, TP.HCM', 17000000, 4, 'MIDDLE', 'Phát triển backend e-commerce với Laravel. Yêu cầu 2+ năm kinh nghiệm PHP, MySQL, Redis.', DATE_ADD(NOW(), INTERVAL -8 DAY), DATE_ADD(NOW(), INTERVAL 32 DAY), true, 9, NOW(), NOW(), 'system', 'system'),
    ('Android Developer', 'Tân Bình, TP.HCM', 20000000, 3, 'MIDDLE', 'Phát triển app Android với Kotlin. Yêu cầu 3+ năm kinh nghiệm, hiểu về Android architecture components.', DATE_ADD(NOW(), INTERVAL -6 DAY), DATE_ADD(NOW(), INTERVAL 38 DAY), true, 9, NOW(), NOW(), 'system', 'system'),
    
    -- MoMo jobs
    ('iOS Developer', 'Quận 7, TP.HCM', 22000000, 3, 'MIDDLE', 'Phát triển app iOS với Swift. Yêu cầu 3+ năm kinh nghiệm, thành thạo UIKit, SwiftUI.', DATE_ADD(NOW(), INTERVAL -4 DAY), DATE_ADD(NOW(), INTERVAL 40 DAY), true, 10, NOW(), NOW(), 'system', 'system'),
    ('Security Engineer', 'Quận 7, TP.HCM', 28000000, 2, 'SENIOR', 'Bảo mật hệ thống fintech. Yêu cầu 4+ năm kinh nghiệm security, hiểu về OWASP, penetration testing.', DATE_ADD(NOW(), INTERVAL -12 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY), true, 10, NOW(), NOW(), 'system', 'system'),
    
    -- VinAI jobs
    ('Computer Vision Engineer', 'Quận 9, TP.HCM', 35000000, 2, 'SENIOR', 'Nghiên cứu và phát triển Computer Vision. Yêu cầu Master/PhD, kinh nghiệm với OpenCV, deep learning.', DATE_ADD(NOW(), INTERVAL -7 DAY), DATE_ADD(NOW(), INTERVAL 50 DAY), true, 11, NOW(), NOW(), 'system', 'system'),
    ('Research Scientist', 'Quận 9, TP.HCM', 40000000, 1, 'SENIOR', 'Nghiên cứu AI/ML. Yêu cầu PhD hoặc 5+ năm kinh nghiệm research, có publication tại conference quốc tế.', DATE_ADD(NOW(), INTERVAL -14 DAY), DATE_ADD(NOW(), INTERVAL 60 DAY), true, 11, NOW(), NOW(), 'system', 'system'),
    
    -- Entry level positions
    ('Junior Java Developer', 'Bình Thạnh, TP.HCM', 10000000, 8, 'FRESHER', 'Fresher/Junior Java developer. Yêu cầu tốt nghiệp IT, hiểu OOP, Java core. Training 2 tháng.', DATE_ADD(NOW(), INTERVAL -2 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), true, 12, NOW(), NOW(), 'system', 'system'),
    ('Frontend Intern', 'Quận 1, TP.HCM', 5000000, 10, 'INTERN', 'Thực tập Frontend 3-6 tháng. Yêu cầu sinh viên năm 3-4, biết HTML/CSS/JavaScript cơ bản.', DATE_ADD(NOW(), INTERVAL -1 DAY), DATE_ADD(NOW(), INTERVAL 45 DAY), true, 13, NOW(), NOW(), 'system', 'system'),
    ('Junior QA Tester', 'Quận 12, TP.HCM', 8000000, 5, 'FRESHER', 'Manual tester fresher. Yêu cầu tốt nghiệp IT hoặc liên quan, có mindset test, tiếng Anh đọc hiểu.', DATE_ADD(NOW(), INTERVAL -3 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY), true, 14, NOW(), NOW(), 'system', 'system'),
    ('Junior PHP Developer', 'Quận 7, TP.HCM', 9000000, 6, 'FRESHER', 'Fresher PHP developer. Yêu cầu biết PHP cơ bản, MySQL, có project cá nhân là lợi thế.', DATE_ADD(NOW(), INTERVAL -5 DAY), DATE_ADD(NOW(), INTERVAL 40 DAY), true, 15, NOW(), NOW(), 'system', 'system'),
    ('Business Analyst Fresher', 'Quận 1, TP.HCM', 10000000, 4, 'FRESHER', 'BA fresher cho dự án fintech. Yêu cầu tiếng Anh tốt, tư duy logic, giao tiếp tốt.', DATE_ADD(NOW(), INTERVAL -4 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), true, 18, NOW(), NOW(), 'system', 'system')
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

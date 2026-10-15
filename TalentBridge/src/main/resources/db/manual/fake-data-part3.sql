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
    ('Junior Java Developer', 'Bình Thạnh, TP.HCM', 10000000, 8, 'ENTRY', 'Fresher/Junior Java developer. Yêu cầu tốt nghiệp IT, hiểu OOP, Java core. Training 2 tháng.', DATE_ADD(NOW(), INTERVAL -2 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), true, 12, NOW(), NOW(), 'system', 'system'),
    ('Frontend Intern', 'Quận 1, TP.HCM', 5000000, 10, 'ENTRY', 'Thực tập Frontend 3-6 tháng. Yêu cầu sinh viên năm 3-4, biết HTML/CSS/JavaScript cơ bản.', DATE_ADD(NOW(), INTERVAL -1 DAY), DATE_ADD(NOW(), INTERVAL 45 DAY), true, 13, NOW(), NOW(), 'system', 'system'),
    ('Junior QA Tester', 'Quận 12, TP.HCM', 8000000, 5, 'ENTRY', 'Manual tester fresher. Yêu cầu tốt nghiệp IT hoặc liên quan, có mindset test, tiếng Anh đọc hiểu.', DATE_ADD(NOW(), INTERVAL -3 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY), true, 14, NOW(), NOW(), 'system', 'system'),
    ('Junior PHP Developer', 'Quận 7, TP.HCM', 9000000, 6, 'ENTRY', 'Fresher PHP developer. Yêu cầu biết PHP cơ bản, MySQL, có project cá nhân là lợi thế.', DATE_ADD(NOW(), INTERVAL -5 DAY), DATE_ADD(NOW(), INTERVAL 40 DAY), true, 15, NOW(), NOW(), 'system', 'system'),
    ('Business Analyst Fresher', 'Quận 1, TP.HCM', 10000000, 4, 'ENTRY', 'BA fresher cho dự án fintech. Yêu cầu tiếng Anh tốt, tư duy logic, giao tiếp tốt.', DATE_ADD(NOW(), INTERVAL -4 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), true, 18, NOW(), NOW(), 'system', 'system')
ON DUPLICATE KEY UPDATE
    updated_at = NOW();

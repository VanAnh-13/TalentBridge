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
    ('Junior Java Developer', 'Bình Thạnh, Ho Chi Minh City', 10000000, 8, 'ENTRY', 'Fresher/junior Java developer. Requires an IT degree, OOP and core Java. Two months of training provided.', DATE_ADD(NOW(), INTERVAL -2 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), true, 12, NOW(), NOW(), 'system', 'system'),
    ('Frontend Intern', 'District 1, Ho Chi Minh City', 5000000, 10, 'ENTRY', '3-6 month frontend internship for 3rd-4th year students with basic HTML/CSS/JavaScript.', DATE_ADD(NOW(), INTERVAL -1 DAY), DATE_ADD(NOW(), INTERVAL 45 DAY), true, 13, NOW(), NOW(), 'system', 'system'),
    ('Junior QA Tester', 'District 12, Ho Chi Minh City', 8000000, 5, 'ENTRY', 'Fresher manual tester. Requires an IT or related degree, a testing mindset and reading English.', DATE_ADD(NOW(), INTERVAL -3 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY), true, 14, NOW(), NOW(), 'system', 'system'),
    ('Junior PHP Developer', 'District 7, Ho Chi Minh City', 9000000, 6, 'ENTRY', 'Fresher PHP developer. Requires basic PHP and MySQL; personal projects are a plus.', DATE_ADD(NOW(), INTERVAL -5 DAY), DATE_ADD(NOW(), INTERVAL 40 DAY), true, 15, NOW(), NOW(), 'system', 'system'),
    ('Business Analyst Fresher', 'District 1, Ho Chi Minh City', 10000000, 4, 'ENTRY', 'Fresher business analyst for fintech projects. Requires good English, logical thinking and communication skills.', DATE_ADD(NOW(), INTERVAL -4 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), true, 18, NOW(), NOW(), 'system', 'system')
ON DUPLICATE KEY UPDATE
    updated_at = NOW();

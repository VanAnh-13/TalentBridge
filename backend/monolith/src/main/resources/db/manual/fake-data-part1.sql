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

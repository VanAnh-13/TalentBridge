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

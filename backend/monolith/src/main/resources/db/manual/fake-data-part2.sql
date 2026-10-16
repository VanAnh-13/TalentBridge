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

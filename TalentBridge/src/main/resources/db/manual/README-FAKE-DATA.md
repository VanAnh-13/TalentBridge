# Hướng dẫn Insert Dữ liệu Giả lập (Fake Data)

## Tổng quan
Đã tạo **130+ bản ghi dữ liệu giả lập** thực tế cho hệ thống TalentBridge:

- **25 Kỹ năng** (Skills): Java, React, Python, Docker, v.v...
- **20 Công ty** (Companies): FPT, VNG, Grab, Shopee, Tiki, MoMo, v.v...
- **30 Người dùng** (Users): 10 Recruiter + 20 Job Seeker
- **25 Việc làm** (Jobs): Từ Fresher đến Senior, đa dạng vị trí IT
- **30 Hồ sơ ứng tuyển** (Resumes): Các trạng thái PENDING, REVIEWING, APPROVED, REJECTED

## Cách chạy

### Option 1: Chạy từng file riêng lẻ
```bash
# Kết nối MySQL
mysql -u root -p talentbridge_db

# Chạy từng phần
source /path/to/fake-data-part1.sql;  # Skills & Companies
source /path/to/fake-data-part2.sql;  # Users
source /path/to/fake-data-part3.sql;  # Jobs  
source /path/to/fake-data-part4.sql;  # Job-Skills & Resumes
```

### Option 2: Chạy file master
```bash
mysql -u root -p talentbridge_db < insert-fake-data.sql
```

### Option 3: Chạy trực tiếp trong Spring Boot
Đặt các file `.sql` trong thư mục `src/main/resources/` và Spring Boot sẽ tự động chạy khi khởi động.

## Thông tin đăng nhập

### Admin
- Email: `admin@talentbridge.vn`
- Password: `password123`

### Recruiter (Nhà tuyển dụng)
- Email: `hr.fpt@fpt.com.vn`
- Password: `password123`

### Job Seeker (Người tìm việc)
- Email: `nguyenvan@gmail.com`
- Password: `password123`

**Lưu ý**: Tất cả users đều dùng password: `password123`

## Dữ liệu đặc biệt

### Công ty nổi bật
- **FPT Software**: Công ty phần mềm lớn nhất VN
- **VNG Corporation**: Chủ sở hữu Zalo, ZaloPay
- **Grab, Shopee, Tiki**: Big tech companies
- **MoMo**: Ví điện tử số 1 VN
- **VinAI**: Viện nghiên cứu AI

### Việc làm đa dạng
- **Level**: ENTRY (Fresher), MIDDLE (2-4 năm), SENIOR (5+ năm)
- **Lương**: 5tr - 40tr VNĐ
- **Vị trí**: Backend, Frontend, Full-stack, DevOps, AI/ML, Mobile
- **Công nghệ**: Java, Python, JavaScript, React, Node.js, .NET, PHP

### Trạng thái hồ sơ
- **PENDING**: Chờ xem xét
- **REVIEWING**: Đang xem xét
- **APPROVED**: Đã chấp nhận
- **REJECTED**: Từ chối

## Kiểm tra dữ liệu

```sql
-- Đếm số lượng bản ghi
SELECT 'Skills' AS Table_Name, COUNT(*) AS Count FROM skills
UNION ALL
SELECT 'Companies', COUNT(*) FROM companies
UNION ALL  
SELECT 'Users', COUNT(*) FROM users
UNION ALL
SELECT 'Jobs', COUNT(*) FROM jobs
UNION ALL
SELECT 'Resumes', COUNT(*) FROM resumes;

-- Xem danh sách công ty
SELECT id, name, address FROM companies LIMIT 10;

-- Xem việc làm đang tuyển
SELECT j.name, c.name as company, j.salary, j.level 
FROM jobs j 
JOIN companies c ON j.company_id = c.id 
WHERE j.active = true 
LIMIT 10;

-- Xem thống kê hồ sơ theo trạng thái
SELECT status, COUNT(*) as count 
FROM resumes 
GROUP BY status;
```

## Lưu ý
- Dữ liệu được thiết kế phù hợp thị trường IT Việt Nam
- Các công ty và việc làm dựa trên thực tế
- Mức lương theo khảo sát thị trường 2024-2025
- Skills phổ biến trong ngành IT hiện tại

# 🚀 TalentBridge - Hệ thống Tuyển dụng IT

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-green.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.3-blue.svg)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.6-blue.svg)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Giới thiệu

TalentBridge là một nền tảng tuyển dụng IT chuyên nghiệp, kết nối nhà tuyển dụng với ứng viên IT tài năng. Hệ thống được xây dựng với công nghệ hiện đại, bảo mật cao và giao diện thân thiện.

**Demo Live**: 
- 🌐 Frontend: https://talentbridge-frontend-273039906673.asia-southeast1.run.app
- 🔧 Backend API: https://talentbridge-273039906673.asia-southeast1.run.app

## ▶️ Chạy toàn bộ hệ thống (Docker)

```bash
cp .env.example .env            # điền JWT_PRIVATE_KEY:  openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 | base64 -w0
docker compose up --build       # web http://localhost:3000, API http://localhost:8080
```

Đặt `BOOTSTRAP_ADMIN_EMAIL` / `BOOTSTRAP_ADMIN_PASSWORD` trong `.env` để tạo quản trị viên đầu tiên.
CI (`.github/workflows/ci.yml`) chạy test backend, test migration trên MariaDB, lint kiến trúc + build frontend và build image.
Kiến trúc: xem [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## ✨ Tính năng chính

### Dành cho Ứng viên
- 🔍 Tìm kiếm việc làm theo kỹ năng, vị trí, mức lương
- 📝 Quản lý hồ sơ và CV online
- 📨 Ứng tuyển nhanh chóng
- 🔔 Nhận thông báo việc làm phù hợp qua email

### Dành cho Nhà tuyển dụng
- 📢 Đăng tin tuyển dụng
- 👥 Quản lý ứng viên và CV
- 📊 Thống kê và báo cáo tuyển dụng
- 🏢 Quản lý thông tin công ty

### Dành cho Admin
- 👤 Quản lý người dùng và phân quyền
- 🏢 Quản lý công ty
- 💼 Quản lý công việc
- 📈 Dashboard thống kê hệ thống

## 🛠 Công nghệ sử dụng

### Backend
- **Java 21** & **Spring Boot 3.5.5**
- **Spring Security** & **JWT** - Xác thực và phân quyền
- **Spring Data JPA** & **Hibernate** - ORM
- **MySQL 8.0** - Database chính
- **Redis** - Caching và session
- **Google Cloud Storage** - Lưu trữ file
- **Spring Boot Mail** - Gửi email

### Frontend
- **React 18.3** & **TypeScript 5.6**
- **Vite** - Build tool
- **TailwindCSS** - Styling
- **Shadcn/ui** - Component library
- **React Router v6** - Routing
- **Axios** - HTTP client
- **React Query** - State management
- **React Hook Form** & **Zod** - Form validation

### DevOps & Cloud
- **Google Cloud Platform**
  - Cloud Run - Hosting
  - Cloud SQL - Database
  - Cloud Storage - File storage
  - Artifact Registry - Docker images
- **Docker** - Containerization
- **GitHub Actions** - CI/CD

## 📁 Cấu trúc dự án

```
do_an_chuyen_nganh/
├── backend/                      # Maven multi-module build (parent pom.xml, one Dockerfile for every module)
│   ├── monolith/                 # Spring Boot app being split up (strangler fig), Clean Architecture modules
│   ├── libs/                     # code shared by the deployables (added as services are extracted)
│   ├── platform/                 # discovery, config and gateway servers
│   └── services/                 # services extracted from the monolith
├── frontend/                     # React + Vite (Clean Architecture per feature)
├── docs/ARCHITECTURE.md          # layers, rules, invariants, real time, AI matching
├── docker-compose.yml            # the whole stack for local use
└── .github/workflows/ci.yml
```

## 🚀 Cài đặt và Chạy

### Yêu cầu hệ thống
- Java 21+
- Node.js 18+
- MySQL 8.0+
- Redis (optional)
- Maven 3.8+

### Backend

```bash
# Clone repository
git clone https://github.com/VanAnh-13/do_an_chuyen_nganh.git
cd do_an_chuyen_nganh/backend

# Copy environment variables
cp .env.example .env

# Cấu hình database trong .env
DB_NAME=talentbridge
DB_USER=root
DB_PASSWORD=yourpassword

# Build và chạy với Maven (from backend/)
mvn clean install
mvn -pl monolith spring-boot:run

# Hoặc chạy với Docker
docker-compose up -d
```

Backend sẽ chạy tại: http://localhost:8080

### Frontend

```bash
cd frontend

# Cài đặt dependencies
npm install

# Copy environment variables
cp .env.example .env

# Cấu hình API URL trong .env
VITE_API_URL=http://localhost:8080

# Chạy development server
npm run dev
```

Frontend sẽ chạy tại: http://localhost:5173

## 📊 Database

### Khởi tạo Database

```sql
-- Tạo database
CREATE DATABASE talentbridge;

-- Import schema và data mẫu
-- The schema and reference data are created by Flyway on start-up (backend/monolith/src/main/resources/db/migration)
```

### Tài khoản test

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@talentbridge.vn | password123 |
| Recruiter | hr.fpt@fpt.com.vn | password123 |
| User | nguyenvan@gmail.com | password123 |

## 🐳 Docker Deployment

### Build và chạy với Docker Compose

```bash
# Development environment
docker-compose up -d

# Production environment
docker-compose -f docker-compose.prod.yml up -d
```

### Build Docker images riêng

```bash
# Backend (any module: MODULE=monolith, platform/api-gateway, ...)
docker build -t talentbridge-backend --build-arg MODULE=monolith backend

# Frontend
cd frontend
docker build -t talentbridge-frontend .
```

## ☁️ Google Cloud Deployment

### Deploy lên Cloud Run

```bash
# Login GCP
gcloud auth login
gcloud config set project YOUR_PROJECT_ID

# Deploy backend (legacy single-image script, see backend/monolith)
cd backend/monolith
gcloud builds submit --config cloudbuild.yaml

# Deploy frontend
cd frontend
gcloud builds submit --config cloudbuild.yaml
```

## 🧪 Testing

### Backend Tests

```bash
cd backend
mvn verify

# Run với coverage
mvn test jacoco:report
```

### Frontend Tests

```bash
cd frontend

# Unit tests
npm run test

# E2E tests với Cypress
npm run cypress:open
```

## 📝 API Documentation

API documentation có sẵn tại: http://localhost:8080/swagger-ui.html

### Các endpoint chính:

#### Authentication
- `POST /api/v1/auth/login` - Đăng nhập
- `POST /api/v1/auth/register` - Đăng ký
- `POST /api/v1/auth/refresh` - Refresh token
- `POST /api/v1/auth/logout` - Đăng xuất

#### Companies
- `GET /companies` - Danh sách công ty
- `GET /companies/{id}` - Chi tiết công ty
- `POST /companies` - Tạo công ty mới
- `PUT /companies/{id}` - Cập nhật công ty

#### Jobs
- `GET /jobs` - Danh sách việc làm
- `GET /jobs/{id}` - Chi tiết việc làm
- `POST /jobs` - Đăng tin tuyển dụng
- `PUT /jobs/{id}` - Cập nhật tin tuyển dụng

#### Resumes
- `GET /resumes` - Danh sách CV
- `POST /resumes/upload` - Upload CV
- `POST /resumes/apply` - Ứng tuyển

## 👥 Team Development

**Nhóm 15** - Đại học Công nghệ Thông tin

### Thành viên:
- **Lê Văn Anh** - Team Lead & Backend Developer
- **[Thành viên 2]** - Frontend Developer
- **[Thành viên 3]** - UI/UX Designer
- **[Thành viên 4]** - Database & DevOps

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Liên hệ

- Email: levananh13062004@gmail.com
- GitHub: [@VanAnh-13](https://github.com/VanAnh-13)

## 🙏 Lời cảm ơn

- Giảng viên hướng dẫn: [Tên giảng viên]
- Khoa Công nghệ Thông tin - Đại học [Tên trường]
- Cộng đồng Spring Boot và React

---

⭐ **Nếu thấy hữu ích, hãy cho project một Star nhé!** ⭐

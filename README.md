# 🚀 TalentBridge - IT Recruitment Platform

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-green.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-green.svg)](https://spring.io/projects/spring-cloud)
[![React](https://img.shields.io/badge/React-19-blue.svg)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.8-blue.svg)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](#-license)

## 📋 About

TalentBridge is an IT recruitment platform that connects employers with IT candidates: companies post jobs,
candidates apply with their CV, recruiters review applications, schedule interviews and chat with candidates.
The interface is available in **English** (default) and **Vietnamese** (EN/VI switch in the header).

**Live demo** (deployed from the earlier single-service version):
- 🌐 Frontend: https://talentbridge-frontend-273039906673.asia-southeast1.run.app
- 🔧 Backend API: https://talentbridge-273039906673.asia-southeast1.run.app

## ▶️ Run the whole system (Docker)

```bash
cp .env.example .env            # fill JWT_PRIVATE_KEY:  openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 | base64 -w0
docker compose up --build       # web http://localhost:3000, API (gateway) http://localhost:8080, Eureka :8761
```

Compose runs MariaDB (one database per service), Redis, Kafka, Eureka, the config server, the API gateway, the
monolith, the notification service and the frontend. The browser only talks to the gateway.

Set `BOOTSTRAP_ADMIN_EMAIL` / `BOOTSTRAP_ADMIN_PASSWORD` in `.env` to create the first administrator.
Every setting of an installation (addresses, CORS origins, time zone, mail, storage, AI matching) is an environment
variable documented in [`.env.example`](.env.example); nothing installation specific is written in the code.

CI (`.github/workflows/ci.yml`) runs the backend tests, a migration test on MariaDB, the frontend architecture lint and
build, and builds the images. Architecture: see [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## ✨ Features

### For candidates
- 🔍 Search jobs by keyword, location, level and skills
- 📝 Manage a profile and apply with a PDF CV
- 🔔 Job alerts for followed skills (e-mail digest) and in-app notifications in real time
- 📅 Answer interview invitations and add them to a calendar (.ics)
- 💬 Chat with the recruiter of each application

### For recruiters
- 📢 Post and manage job offers for their company, manage company members
- 👥 Review applications, change their status
- ✨ AI CV-job matching: candidates ranked by how well their CV fits the job (Claude API, optional)
- 📅 Propose interview slots, reschedule or cancel

### For administrators
- 👤 Users, roles and permissions (`METHOD /path` permissions, editable at run time)
- 🏢 Companies, 💼 jobs, 🛠️ skills, 📄 applications
- 📈 Admin dashboard

## 🛠 Tech stack

### Backend (`backend/`, Maven multi-module)
- **Java 21**, **Spring Boot 3.5.5**, **Spring Cloud 2025.0.0** (Gateway, Eureka, Config Server)
- **Spring Security** with **RS256 JWT** (public keys at `/.well-known/jwks.json`)
- **Spring Data JPA**, **Flyway** migrations, **MariaDB** (H2 in development)
- **Kafka** events through a transactional outbox, **Redis** (refresh tokens, cache)
- **STOMP over WebSocket** for live notifications and chat
- **Claude API** (Anthropic Java SDK) for CV-job matching, **Google Cloud Storage** or local disk for files
- **Spring Mail** + Thymeleaf e-mails

### Frontend (`frontend/`)
- **React 19**, **TypeScript 5.8**, **Vite 6**, **React Router 7**, **Redux Toolkit**
- **Tailwind CSS 4** and **shadcn/ui**, **Axios**, **@stomp/stompjs**
- **i18next** / **react-i18next**: English and Vietnamese, with type-checked message keys
- Clean Architecture per feature, enforced by ESLint import rules

### DevOps
- **Docker** / **Docker Compose**, **GitHub Actions**
- Google Cloud (Cloud Run, Cloud SQL, Cloud Storage, Artifact Registry) scripts for the monolith in `backend/monolith`

## 📁 Project structure

```
TalentBridge/
├── backend/                      # Maven multi-module build (parent pom.xml, one Dockerfile for every module)
│   ├── monolith/                 # Spring Boot app being split up (strangler fig), Clean Architecture modules
│   ├── libs/                     # code shared by the deployables (common-lib, security-lib, event-contracts)
│   ├── platform/                 # discovery, config and gateway servers
│   └── services/                 # services extracted from the monolith (notification-service)
├── frontend/                     # React + Vite (Clean Architecture per feature, i18n catalogues per feature)
├── infra/                        # database initialisation for docker compose
├── docs/ARCHITECTURE.md          # layers, rules, invariants, events, real time, AI matching, languages
├── docker-compose.yml            # the whole stack for local use
└── .github/workflows/ci.yml
```

## 🚀 Development setup

### Requirements
- Java 21+ (Maven comes with the `mvnw` wrapper)
- Node.js 20+
- Docker, for the full stack (MariaDB, Redis and Kafka are only needed there)

### Backend

```bash
cd backend
./mvnw clean install -DskipTests
./mvnw -pl monolith spring-boot:run      # http://localhost:8080, in-memory H2 database, throw-away signing key
```

The development profile creates an administrator (`dev-admin@talentbridge.com` / `Admin@123456`, see
`application.properties`) and logs password-reset links instead of e-mailing them. The platform servers and the
notification service are off in this mode; use docker compose for the whole system.

### Frontend

```bash
cd frontend
npm install
cp .env.example .env      # VITE_API_URL=http://localhost:8080 (the gateway, or the monolith in a plain local run)
npm run dev               # http://localhost:3000
```

## 📊 Database

The schema and reference data are created by **Flyway** on start-up
(`backend/monolith/src/main/resources/db/migration`); applied migrations are never edited, changes go into a new one.

Sample data (companies, jobs, users, applications) can be loaded by hand into a development database, see
[`db/manual/README-FAKE-DATA.md`](backend/monolith/src/main/resources/db/manual/README-FAKE-DATA.md). Its accounts all
use the password `password123`:

| Role | Email |
|------|-------|
| Admin | admin@talentbridge.vn |
| Recruiter | hr.fpt@fpt.com.vn |
| Candidate | nguyenvan@gmail.com |

## 🐳 Docker images

```bash
# Backend (any module: MODULE=monolith, platform/api-gateway, services/notification-service, ...)
docker build -t talentbridge-backend --build-arg MODULE=monolith backend

# Frontend (the API address is baked into the bundle)
docker build -t talentbridge-frontend --build-arg API_BASE_URL=http://localhost:8080 frontend
```

## ☁️ Google Cloud deployment

The scripts in `backend/monolith` deploy the monolith to Cloud Run. They read every installation-specific value
(project, region, bucket, CORS origins, web app address, signing key) from the environment, see
`backend/monolith/gcp-env.sh`:

```bash
gcloud auth login
export PROJECT_ID=my-project ALLOWED_ORIGINS=https://app.example.com FRONTEND_URL=https://app.example.com
export JWT_PRIVATE_KEY="$(openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 | base64 -w0)"
backend/monolith/deploy-gcp.sh            # Cloud SQL + Memorystore + Cloud Run

# Frontend
cd frontend
gcloud builds submit --config cloudbuild.yaml --substitutions=_API_BASE_URL=https://api.example.com
```

## 🧪 Testing

```bash
# Backend: unit, API and architecture tests on H2 (the MariaDB migration test runs in CI)
cd backend
./mvnw verify

# Frontend: architecture lint, type check and production build
cd frontend
npm run lint
npm run build
```

End-to-end Cypress specs live in `frontend/cypress/e2e` (`run-frontend-tests.sh` installs Cypress and runs them
against `npm run dev`).

## 📝 API documentation

Swagger UI: http://localhost:8080/swagger-ui/index.html (monolith; OpenAPI JSON at `/v3/api-docs`). Main endpoints (no prefix, e.g. `POST /auth/login`):

| Area | Endpoints |
|------|-----------|
| Authentication | `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh-token`, `POST /auth/logout`, `POST /auth/forgot-password`, `POST /auth/reset-password`, `GET /auth/sessions` |
| Companies | `GET /companies`, `GET /companies/{id}`, `POST /companies`, `PUT /companies/{id}`, `GET /companies/me/users` |
| Jobs and skills | `GET /jobs`, `GET /jobs/{id}`, `POST /jobs`, `PUT /jobs/{id}`, `GET /skills` |
| Applications | `GET /resumes`, `POST /resumes`, `GET /resumes/me`, `PUT /resumes/status` |
| Interviews | `POST /interviews`, `GET /interviews/me`, `PUT /interviews/{id}/confirm`, `PUT /interviews/{id}/reschedule`, `GET /interviews/{id}/calendar.ics` |
| Matching | `GET /matching/jobs/{jobId}/candidates`, `GET /matching/me/recommendations` |
| Chat | `GET /chats`, `GET /chats/{id}/messages`, `POST /chats/{id}/messages` |
| Notifications (notification-service) | `GET /notifications`, `GET /notifications/unread-count`, `POST /notifications/read-all`, WebSocket `/ws` |

## 👥 Team

**Team 15** - University of Information Technology

### Members
- **Lê Văn Anh** - Team Lead & Backend Developer
- **[Member 2]** - Frontend Developer
- **[Member 3]** - UI/UX Designer
- **[Member 4]** - Database & DevOps

## 📄 License

This project is released under the MIT License.

## 📞 Contact

- Email: levananh13062004@gmail.com
- GitHub: [@VanAnh-13](https://github.com/VanAnh-13)

## 🙏 Acknowledgements

- Supervisor: [Supervisor name]
- Faculty of Information Technology - [University name]
- The Spring Boot and React communities

---

⭐ **If you find this project useful, please give it a star!** ⭐

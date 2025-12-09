# TalentBridge

A full-stack job recruitment platform connecting employers with job seekers. Built with Spring Boot and React.

## Features

### For Job Seekers
- Browse and search job listings by skills, location, and salary
- View company profiles and their open positions
- Create account and manage profile
- Upload resumes and apply to jobs
- Subscribe to job alerts based on preferred skills
- Manage multiple sessions across devices

### For Recruiters
- Create and manage company profile
- Post job listings with required skills and salary ranges
- Review candidate resumes and applications
- Invite team members to manage company jobs
- Track application status

### For Administrators
- Full user management (create, update, delete)
- Company oversight and management
- Role and permission configuration
- System-wide job and resume management
- Access control with granular permissions

## Tech Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.5.5 | Application framework |
| Java | 21 | Language |
| Spring Security | - | Authentication & Authorization |
| Spring Data JPA | - | ORM / Data access |
| H2 / MySQL | - | Database (dev / prod) |
| Redis | - | Caching (production) |
| SpringDoc OpenAPI | 2.8.9 | API documentation |
| Lombok | - | Boilerplate reduction |
| Thymeleaf | - | Email templates |
| Google Cloud Storage | - | File storage |

### Frontend
| Technology | Version | Purpose |
|------------|---------|---------|
| React | 19 | UI framework |
| TypeScript | 5.8 | Language |
| Vite | 6 | Build tool |
| Tailwind CSS | 4 | Styling |
| Redux Toolkit | - | State management |
| React Router | 7 | Routing |
| Axios | - | HTTP client |
| Radix UI | - | UI primitives |
| React Quill | - | Rich text editor |
| Vitest | - | Unit testing |
| Cypress | - | E2E testing |

## Project Structure

```
├── TalentBridge/                    # Backend
│   ├── src/main/java/.../
│   │   ├── controller/              # REST endpoints
│   │   ├── service/                 # Business logic
│   │   ├── repository/              # Data access
│   │   ├── model/                   # JPA entities
│   │   ├── dto/                     # Request/Response objects
│   │   ├── config/                  # Spring configuration
│   │   ├── advice/                  # Exception handlers
│   │   └── util/                    # Utilities
│   ├── src/main/resources/
│   │   ├── application.properties   # Dev config
│   │   ├── application-prod.properties
│   │   └── templates/               # Email templates
│   └── init-db/                     # SQL scripts
│
└── TalentBridge-Frontend/           # Frontend
    └── src/
        ├── components/
        │   ├── ui/                  # Reusable UI (shadcn/ui)
        │   └── custom/              # App-specific components
        ├── pages/
        │   ├── client/              # Public pages
        │   ├── admin/               # Admin dashboard
        │   ├── recruiter/           # Recruiter portal
        │   └── user/                # User account
        ├── services/                # API modules
        ├── features/                # Redux slices
        ├── types/                   # TypeScript definitions
        └── lib/                     # Axios client, utils
```

## Getting Started

### Prerequisites

- Java 21 (JDK)
- Node.js 18+
- npm or yarn
- Docker (optional, for containerized setup)

### Environment Setup

1. Copy the environment template:
```bash
cd TalentBridge
cp .env.example .env
```

2. Update `.env` with your configuration:
```env
# Required
JWT_SECRET=your-secure-secret-key-at-least-256-bits

# Optional - Database (defaults to H2 for dev)
DB_NAME=talentbridge
DB_USER=your_user
DB_PASSWORD=your_password

# Optional - Email
MAIL_HOST=smtp.gmail.com
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_app_password

# Optional - Google Cloud Storage
GCS_PROJECT_ID=your_project
GCS_BUCKET_NAME=your_bucket
```

### Running Locally

#### Backend

```bash
cd TalentBridge

# Run with Maven wrapper
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

Backend runs at `http://localhost:8080`

#### Frontend

```bash
cd TalentBridge-Frontend

# Install dependencies
npm install

# Start dev server
npm run dev
```

Frontend runs at `http://localhost:3000`

### Running with Docker

```bash
cd TalentBridge

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f
```

## Development

### Backend Commands

```bash
cd TalentBridge

./mvnw spring-boot:run           # Run dev server
./mvnw test                      # Run all tests
./mvnw test -Dtest=ClassName     # Run specific test
./mvnw package                   # Build JAR
./mvnw package -DskipTests       # Build without tests
./mvnw clean install             # Clean build
```

### Frontend Commands

```bash
cd TalentBridge-Frontend

npm run dev              # Start dev server (port 3000)
npm run build            # Production build
npm run preview          # Preview production build
npm run test             # Run tests (single run)
npm run test:watch       # Run tests (watch mode)
npm run test:coverage    # Run tests with coverage
npm run lint             # ESLint check
```

## API Documentation

When the backend is running, access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### API Response Format

```json
{
  "statusCode": 200,
  "message": "Success",
  "data": { ... },
  "errorCode": null
}
```

### Authentication

The API uses JWT Bearer tokens:

```
Authorization: Bearer <access_token>
```

Tokens are obtained via `/api/v1/auth/login` and refreshed via `/api/v1/auth/refresh`.

## Testing

### Backend Testing

- **Unit Tests**: JUnit 5 + Mockito
- **Integration Tests**: Spring Test
- **Property-Based Tests**: jqwik

```bash
./mvnw test
```

### Frontend Testing

- **Unit Tests**: Vitest + fast-check (property-based)
- **E2E Tests**: Cypress

```bash
# Unit tests
npm run test

# E2E tests
npx cypress open
```

## Deployment

### Docker Build

```bash
# Backend
cd TalentBridge
docker build -t talentbridge-backend .

# Frontend
cd TalentBridge-Frontend
docker build --build-arg API_BASE_URL=https://api.example.com -t talentbridge-frontend .
```

### Google Cloud Platform

```bash
# Deploy using Cloud Build
gcloud builds submit --config=cloudbuild.yaml
```

### Kubernetes

```bash
kubectl apply -f TalentBridge/k8s/deployment.yml
```

## Configuration

### Backend Profiles

- `default` - H2 in-memory database, simple cache
- `prod` - MySQL, Redis cache, GCS storage

Activate with:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Key Configuration Options

| Property | Description | Default |
|----------|-------------|---------|
| `jwt.access-token-expiration` | Access token TTL (seconds) | 300 |
| `jwt.refresh-token-expiration` | Refresh token TTL (seconds) | 86400 |
| `spring.servlet.multipart.max-file-size` | Max upload size | 10MB |
| `app.cors.allowed-origins` | CORS origins | localhost:3000,5173 |

## License

This project is for educational purposes.

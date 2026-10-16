# Architecture

TalentBridge follows **Clean Architecture** in both the backend and the frontend. The code is migrated module by
module; modules not yet migrated are listed at the bottom. `ArchitectureTest` (backend) enforces the rules below for
every migrated module.

## Backend (`backend/monolith/`, Spring Boot)

```
com.example.talentbridge
├── shared/                      kernel used by every module
│   ├── domain/    exception/ (NotFound, AlreadyExists, Forbidden, InvalidRequest, BusinessRuleViolation)
│   │              query/     (PageQuery, PageResult, SortOrder, FilterExpression)  -- framework free
│   ├── infrastructure/persistence/  BaseEntity (audit columns), JPA auditing, FilterSpecifications, PagingMapper
│   └── presentation/  ApiResponse, ApiResponseAdvice, GlobalExceptionHandler, PageResponseDto, PageRequests,
│                      security/ (HTTP security chain, CORS, JSON 401 entry point)
└── <module>/                    skill, ... (one per business capability)
    ├── domain/          entities + value objects (plain Java) and the *ports* they need (e.g. SkillRepository)
    ├── application/     use cases: one class per operation (CreateSkill, SearchSkills, ...)
    ├── infrastructure/  adapters: JPA entities (`*JpaEntity`), Spring Data repos, `*RepositoryAdapter`, gateways
    └── presentation/    REST controllers + request/response DTOs (Bean Validation, Swagger, @PreAuthorize)
```

Dependency rule (checked by ArchUnit): `presentation → application → domain ← infrastructure`.

| Layer | May use | Must not use |
|---|---|---|
| domain | `java.*`, Lombok, `shared.domain` | Spring, JPA, Jackson, HTTP, other modules |
| application | domain, `@Service`/`@Transactional` | JPA, Spring Data/Web/Security, Jackson, any adapter |
| infrastructure | domain, application, Spring Data/JPA | presentation |
| presentation | application, domain, Spring Web/Security | infrastructure |

* A module may use another module's `domain`/`application`, never its `infrastructure`/`presentation`. The single
  exception is a JPA association: an entity may name another module's `*JpaEntity` class (the tables are shared).
* Outbound ports that a use case needs but that are not repositories (e.g. `SkillCatalog`, `CurrentUser`) live in
  `application/port` (or `shared.application`); repository ports live in `domain`.
* **Domain objects are not JPA entities.** The JPA entity (`XJpaEntity`) lives in `infrastructure/persistence`; the
  `XRepositoryAdapter` maps between both and implements the port declared in `domain`. On update it loads the managed
  entity and copies the fields, so relations the domain does not model are preserved.
* **Queries by client filter:** controllers pass the raw `filter` string as a `FilterExpression`; only
  `FilterSpecifications` (infrastructure) turns it into a JPA `Specification`. Paging uses `PageQuery`/`PageResult`.
* **Errors:** use cases throw the `shared.domain.exception` types; `GlobalExceptionHandler` maps them to HTTP
  (404 `ENTITY_NOT_FOUND`, 409 `ENTITY_ALREADY_EXISTS` / `BUSINESS_RULE_VIOLATION`, 403 `ACCESS_DENIED`,
  400 `VALIDATION_ERROR`).
* **Who is calling** is an application concern behind a port (not `SecurityContextHolder` in use cases).
* Adding a module = add it to `MIGRATED` in `ArchitectureTest` in the same commit.

## Frontend (`frontend/`, React + Vite)

```
src
├── app/                        composition root and app shell
│   ├── container.ts            the ONLY place that wires ports to adapters (use cases are exported from here)
│   ├── wiring.ts               HTTP client <-> auth use cases <-> redux store
│   ├── store.ts, router.tsx, App.tsx, main.tsx
│   ├── layouts/                admin / recruiter / client / user shells
│   └── pages/                  pages that compose several features (home, dashboard)
├── shared/
│   ├── domain/                 Page, PageQuery, ApiError/errorMessage, filter builders (contains, allOf)
│   ├── infrastructure/http/    axios client (token refresh), `api` helper that unwraps the response envelope
│   └── presentation/           ui/ (shadcn), components/, hooks/, format helpers
└── features/<name>/            access, auth, chat, company, interview, job, matching, notification, resume, skill, subscriber, user
    ├── domain/                 entities and value types (plain TypeScript)
    ├── application/            ports.ts (repository interfaces) + <name>UseCases.ts (createXUseCases(repository))
    ├── infrastructure/         Http<Name>Repository: the only code that knows URLs, multipart and wire quirks
    ├── presentation/           pages/, components/, state/ (redux, auth only)
    └── index.ts                public API: what other features and the app may import
```

Dependency rule (enforced by `no-restricted-imports` blocks generated per layer and feature in `eslint.config.js`;
`npm run lint` fails on a violation): `presentation -> application -> domain <- infrastructure`.

| Layer | May use | Must not use |
|---|---|---|
| domain | `shared/domain`, same-feature domain | React, axios, redux, any other layer |
| application | domain | React, axios, adapters, UI, the composition root |
| infrastructure | domain, application, `shared/infrastructure` | React/UI, presentation |
| presentation | application, domain, `shared/*`, `@/app/container` | infrastructure, other `@/app/*` files |

* Screens never build URLs, FormData or filter expressions: they call a use case (`users.search(...)`) obtained from
  `@/app/container`, and get domain objects back.
* Errors reach the UI as `ApiError`; `errorMessage(err, fallback)` picks the server's message.
* Features are isolated: another feature is reached through `@/features/<name>` (UI, presentation only) or its
  `domain` types, never through its application/infrastructure/presentation files.
* Adding a feature = a new folder with the four layers; the ESLint config discovers it automatically.

### Languages (i18n)
The interface is in English by default and in Vietnamese on request (the EN/VI switch in every header; the choice is
kept in `localStorage` under `talentbridge.language` and sets `<html lang>`). It uses i18next + react-i18next:
* **Catalogues**: one namespace per feature in `features/<name>/presentation/i18n.ts` (exported by the feature's
  `index.ts`), `common` (`shared/presentation/i18n/common.ts`: buttons, field names, statuses, levels) and `app`
  (`app/i18n/app.ts`: layouts and home page), all registered in `app/i18n/resources.ts`.
* **Type safety**: keys and interpolation values of `t()` are checked against the English catalogue
  (`app/i18n/i18next.d.ts`), and each `vi` catalogue is typed `Translation<typeof en>`, so a missing or extra key
  fails `tsc`.
* **Layers**: domain and application code stay framework free, so they return codes (e.g. `OfferProblem`) that
  presentation turns into text. Dates and numbers use `currentLocale()` (`en-GB` or `vi-VN`).
* Messages coming from the API (errors, notifications) are written by the backend, in English.
* Adding a language: add its code to `LANGUAGES` and `LOCALES` (`shared/presentation/i18n/types.ts`), its name under
  `language` in `common`, a catalogue per namespace, and its entry in `resources.ts`; the switcher lists `LANGUAGES`.

### Status
Backend: every monolith package follows the four layers (`shared`, `access`, `auth`, `chat`, `company`, `digest`,
`interview`, `job`, `matching`, `resume`, `skill`, `subscriber`, `user`), and so do the notification service's
(`notification`, `realtime`); each deployable has its own `ArchitectureTest`.
Frontend: every feature follows the four layers and no legacy `pages/`, `services/`, `types/` remains.

## Platform (Spring Cloud)
```
browser ──► api-gateway :8080 ─┬─ /notifications/**, /ws ──► notification-service :8082 ──► notification_db
                                └─ everything else ──────────► monolith :8081 ──► talentbridge DB, Redis
                                                                   │ outbox ──► Kafka ──► notification-service
             Eureka discovery-server :8761 ◄── every service registers (routes use lb://<name>)
             config-server :8888 (native, backend/platform/config-server/.../config-repo)
```
* **api-gateway** (Spring Cloud Gateway, WebFlux) is the only public entry point. Routes live in its
  `application.yml`: `/notifications/**` and the `/ws` WebSocket go to the notification service, everything else to
  `lb://monolith` (strangler fig: each extraction adds a more specific route). It answers CORS (`ALLOWED_ORIGINS`) before routing, deduplicates the CORS headers services
  add, gives every request an `X-Request-Id` (kept if the caller sent a sane one, returned in the response, forwarded
  to the service) and hides the services' `/actuator/**`.
* **discovery-server** (Eureka): services register under their `spring.application.name`.
* **config-server** serves `config-repo/application.yml` (every service: Eureka address, JWKS address, management) and
  `config-repo/<service>.yml`. Secrets never go there; container environment variables win over it.
* Platform clients are off by default (`CONFIG_SERVER_ENABLED`, `EUREKA_ENABLED`), so a plain `mvn spring-boot:run`
  and the tests need none of it; docker compose switches them on.

## Tokens
* The auth module issues RS256 access and refresh tokens signed with an RSA private key (`JWT_PRIVATE_KEY`, PKCS#8 PEM;
  development falls back to a throw-away key). Claims: `iss` (`talentbridge-auth`), `sub` (email), `user`
  (id, email, name, companyId, role, ...), `permissions` ("METHOD /path" strings), `jti`, `iat`, `exp`. The header names
  the key (`kid`, the key's RFC 7638 thumbprint unless `JWT_KEY_ID` is set).
* The public key is published at `GET /.well-known/jwks.json` (no auth, cached 5 min). Other services only set
  `jwt.jwk-set-uri`: `backend/libs/security-lib` then verifies signature, issuer and expiry, maps `permissions` to
  authorities (so `@PreAuthorize("hasAuthority('GET /x')")` works everywhere) and exposes the caller as `CurrentUser`.

## Safety invariants (enforced in the domain/use cases, covered by tests)
* The ADMIN role always keeps `Role.ADMIN_ESSENTIALS` (open the admin workspace, manage roles/permissions/users);
  those permissions cannot be deleted or re-pointed, and the last administrator cannot be deleted or demoted.
* Changing or resetting a password signs the account out everywhere else (refresh tokens are revoked; access tokens
  expire on their own). Deleting a user revokes all of their sessions.
* A skill required by a job cannot be deleted; deleting a skill only stops subscribers from following it.
* CVs are shown with the browser's own PDF viewer, never parsed by JavaScript in the page.

## Services and events
| Deployable | Owns | Database | Talks to others by |
|---|---|---|---|
| `backend/monolith` | auth, users, access control, companies, jobs, skills, subscribers, resumes, interviews, chat, AI matching, job-digest e-mail | `talentbridge` | REST via the gateway; publishes events through its outbox |
| `backend/services/notification-service` | in-app notifications (inbox) and the browsers' live channel (STOMP over WebSocket at `/ws`), presence | `notification_db` | consumes events; REST `/notifications/**` |

**Events** (`backend/libs/event-contracts`, JSON envelope `eventId, type, version, occurredAt, payload`, Kafka key =
user id so one user's events stay in order):
* `talentbridge.notifications.v1` / `NotificationRequested(userId, type, title, body, link, onlyIfOffline)`: put a
  message in someone's inbox and push it live (`/user/queue/notifications`). `onlyIfOffline` (chat) is dropped while the
  user is connected, because they already saw the message live.
* `talentbridge.realtime.v1` / `RealtimePush(userId, channel, data)`: deliver `data` live on `/user/queue/<channel>`
  (`chat` new messages, `chat-read` read receipts); nothing is stored.

**Producing (monolith):** adapters behind the existing output ports (`ApplicationNotifier`, interview `Notifier`, chat
`Notifier`/`ChatPublisher`) call `shared.infrastructure.messaging.Outbox`, which inserts into `outbox_events` in the
business transaction. `OutboxRelay` ships rows to Kafka in insertion order (`EVENTS_RELAY_ENABLED`); a failed send
stops the batch and is retried. Without the relay (plain local run, tests) events wait in the table.
**Consuming (notification-service):** at-least-once delivery, so a notification is stored once per `eventId` (unique
column) and live pushes are de-duplicated in memory; unreadable messages are logged and skipped, storage failures are
retried 5 times.

**Real time:** the browser opens one STOMP connection to `/ws` (through the gateway, which proxies the WebSocket to
`lb:ws://notification-service`) and authenticates in the CONNECT frame (`Authorization: Bearer <access token>`, checked
with the JWKS); the principal name is the user id; clients may only subscribe to their own `/user/queue/*`.
The broker is in memory, so this service runs as a single instance: scaling out needs a broker relay (e.g. RabbitMQ
STOMP) or a shared presence store.

**Moving existing notifications** (an installation that ran the monolith before the split): the old `notifications`
table stays in the monolith database untouched. Copy it once into the service's database, e.g. on MariaDB:
`INSERT INTO notification_db.notifications (user_id, type, title, body, link, is_read, created_at)
SELECT user_id, type, title, body, link, is_read, created_at FROM talentbridge.notifications;`

## AI CV-job matching
The `matching` module scores how well an application's CV fits its job, with the Claude API
(`matching.infrastructure.claude.ClaudeCvMatcher`, behind the `CvMatcher` port).
* The CV is sent to the model as a PDF document next to the job's title, level, location, skills and description; the
  answer is requested as JSON (`score`, `matchedSkills`, `missingSkills`, `summary`) and the score is clamped to 0-100.
  The system prompt tells the model the CV is untrusted data and must not be obeyed.
* It is **off until `ANTHROPIC_API_KEY` is set**: no network traffic, `GET /matching/status` says `enabled: false`, and
  submitting a CV is unaffected. `MATCHING_MODEL` picks the model (default `claude-opus-5-5`); `ANTHROPIC_BASE_URL`
  points at a gateway or a stand-in server. Every call costs money, so scoring runs once per CV submission/replacement
  (recruiters may ask again with `POST /matching/resumes/{id}/recompute`).
* Scoring runs on a small background pool after the submitting transaction commits and holds no database transaction
  while the model works. A failure is logged and leaves the application unscored.
* Results live in `match_results` (one current row per application). Recruiters of the job's company, and admins,
  read them; candidates never see their own score. `GET /matching/me/recommendations` is a plain skill overlap with the
  skills the candidate follows (no model involved).

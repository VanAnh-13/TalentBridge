# Architecture

TalentBridge follows **Clean Architecture** in both the backend and the frontend. The code is migrated module by
module; modules not yet migrated are listed at the bottom. `ArchitectureTest` (backend) enforces the rules below for
every migrated module.

## Backend (`TalentBridge/`, Spring Boot)

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

## Frontend (`TalentBridge-Frontend/`, React + Vite)

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
└── features/<name>/            access, auth, company, job, resume, skill, subscriber, user
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

### Status
Backend: every package follows the four layers (`shared`, `skill`, `subscriber`, `access`, `company`, `job`, `resume`,
`user`, `auth`, `notification`), checked by `ArchitectureTest`.
Frontend: every feature follows the four layers and no legacy `pages/`, `services/`, `types/` remains.

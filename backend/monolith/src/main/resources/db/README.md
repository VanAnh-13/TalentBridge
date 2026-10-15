# Database migrations

Schema and reference data are owned by **Flyway** (`db/migration`, `V<n>__<name>.sql`). Hibernate only
validates (`ddl-auto=validate` in dev/tests; `validate` in prod too; override with
`SPRING_JPA_HIBERNATE_DDL_AUTO=none` only as an emergency escape hatch).

| Version | Purpose |
|---|---|
| V1 | Baseline schema, identical to what Hibernate used to create |
| V2 | Roles, permissions, role→permission assignments (idempotent) |
| V3 | Removes duplicate permission rows and adds `UNIQUE(api_path, method)` |

* **Existing databases** (created before Flyway) are baselined at V1 automatically
  (`spring.flyway.baseline-on-migrate=true`) and only receive V2+.
* **Never edit an applied migration**; add a new `V<n+1>__*.sql`.
* Changing a JPA entity requires a matching migration, otherwise the dev/test startup fails validation.
* `db/manual/` holds optional, hand-run scripts (demo data, logo fixes). They are **not** run automatically.
* No admin user is seeded: set `BOOTSTRAP_ADMIN_EMAIL` / `BOOTSTRAP_ADMIN_PASSWORD` (see `.env.example`).
* Run the real-MariaDB migration test with the command in `MariaDbMigrationTest`.

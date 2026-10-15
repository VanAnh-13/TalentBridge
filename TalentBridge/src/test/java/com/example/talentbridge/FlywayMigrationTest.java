package com.example.talentbridge;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Runs the real migrations against throw-away H2 databases (MySQL mode). */
class FlywayMigrationTest {

    private static String newUrl() {
        return "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";
    }

    private static Flyway flyway(String url, boolean baselineOnMigrate, String target) {
        return Flyway.configure()
                .dataSource(url, "sa", "")
                .locations("classpath:db/migration")
                .baselineOnMigrate(baselineOnMigrate)
                .baselineVersion("1")
                .target(target)
                .load();
    }

    private static long count(Connection c, String sql) throws SQLException {
        try (Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            rs.next();
            return rs.getLong(1);
        }
    }

    @Test
    @DisplayName("Fresh database: all migrations apply and reference data is complete without duplicates")
    void freshDatabase() throws Exception {
        String url = newUrl();
        flyway(url, false, "latest").migrate();

        try (Connection c = DriverManager.getConnection(url, "sa", "")) {
            assertEquals(3, count(c, "SELECT COUNT(*) FROM roles"));
            assertEquals(69, count(c, "SELECT COUNT(*) FROM permissions"));
            assertEquals(68, count(c, "SELECT COUNT(*) FROM roles_permissions rp JOIN roles r ON r.id = rp.role_id WHERE r.name = 'ADMIN'"));
            assertEquals(31, count(c, "SELECT COUNT(*) FROM roles_permissions rp JOIN roles r ON r.id = rp.role_id WHERE r.name = 'RECRUITER'"));
            assertEquals(25, count(c, "SELECT COUNT(*) FROM roles_permissions rp JOIN roles r ON r.id = rp.role_id WHERE r.name = 'USER'"));
            assertEquals(0, count(c, "SELECT COUNT(*) FROM users"), "no default users may be seeded");

            String holders = "SELECT COUNT(*) FROM roles_permissions rp JOIN roles r ON r.id = rp.role_id "
                    + "JOIN permissions p ON p.id = rp.permission_id WHERE p.api_path = '%s' AND r.name = '%s'";
            assertEquals(1, count(c, holders.formatted("/admin", "ADMIN")), "ADMIN opens the admin workspace");
            assertEquals(0, count(c, holders.formatted("/admin", "RECRUITER")));
            assertEquals(1, count(c, holders.formatted("/recruiter", "RECRUITER")), "RECRUITER opens the recruiter workspace");
            assertEquals(0, count(c, holders.formatted("/recruiter", "ADMIN")));
            assertEquals(0, count(c, holders.formatted("/admin", "USER")) + count(c, holders.formatted("/recruiter", "USER")));
        }
    }

    @Test
    @DisplayName("Legacy production database (pre-Flyway, duplicated permissions) is baselined and repaired")
    void legacyDatabaseWithDuplicates() throws Exception {
        String url = newUrl();

        // 1) Simulate the old world: V1 schema + the old data.sql re-run three times, no Flyway history.
        flyway(url, false, "1").migrate();
        try (Connection c = DriverManager.getConnection(url, "sa", ""); Statement s = c.createStatement()) {
            s.execute("DROP TABLE flyway_schema_history");
            s.execute("INSERT INTO roles (name, description, active) VALUES ('ADMIN','a',true),('RECRUITER','r',true),('USER','u',true)");
            for (int i = 0; i < 3; i++) {
                s.execute("INSERT INTO permissions (name, api_path, method, module) VALUES "
                        + "('Create Role','/roles','POST','ROLE'),('Get Role List','/roles','GET','ROLE'),"
                        + "('Get Job List','/jobs','GET','JOB')");
                s.execute("INSERT INTO roles_permissions (role_id, permission_id) "
                        + "SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'ADMIN' "
                        + "AND NOT EXISTS (SELECT 1 FROM roles_permissions x WHERE x.role_id = r.id AND x.permission_id = p.id)");
            }
            assertEquals(9, count(c, "SELECT COUNT(*) FROM permissions"));
        }

        // 2) Deploy the Flyway version: baseline at 1, run V2 + V3.
        flyway(url, true, "latest").migrate();

        try (Connection c = DriverManager.getConnection(url, "sa", "")) {
            assertEquals(3, count(c, "SELECT COUNT(*) FROM roles"), "roles must not be duplicated");
            assertEquals(69, count(c, "SELECT COUNT(*) FROM permissions"), "duplicates removed, catalog completed");
            assertEquals(0, count(c, "SELECT COUNT(*) FROM (SELECT api_path, method FROM permissions GROUP BY api_path, method HAVING COUNT(*) > 1) d"));
            assertEquals(68, count(c, "SELECT COUNT(*) FROM roles_permissions rp JOIN roles r ON r.id = rp.role_id WHERE r.name = 'ADMIN'"));
            assertEquals(0, count(c, "SELECT COUNT(*) FROM roles_permissions rp WHERE rp.permission_id NOT IN (SELECT id FROM permissions)"));
            // V4: the old seed let recruiters modify any company's data; that grant must be gone
            assertEquals(0, count(c, "SELECT COUNT(*) FROM roles_permissions rp JOIN roles r ON r.id = rp.role_id JOIN permissions p ON p.id = rp.permission_id "
                    + "WHERE r.name = 'RECRUITER' AND ((p.method = 'PUT' AND p.api_path = '/jobs/{id}') OR (p.method = 'DELETE' AND p.api_path = '/companies/{id}'))"));
            assertEquals(1, count(c, "SELECT COUNT(*) FROM roles_permissions rp JOIN roles r ON r.id = rp.role_id JOIN permissions p ON p.id = rp.permission_id "
                    + "WHERE r.name = 'RECRUITER' AND p.method = 'PUT' AND p.api_path = '/jobs/company/{id}'"));

            // the unique key now prevents the old startup re-insert from ever duplicating again
            try (Statement s = c.createStatement()) {
                assertThrows(SQLException.class, () -> s.execute(
                        "INSERT INTO permissions (name, api_path, method, module) VALUES ('Dup','/roles','POST','ROLE')"));
            }
        }
    }

    @Test
    @DisplayName("Migrations are idempotent when re-run")
    void rerunIsNoop() {
        String url = newUrl();
        flyway(url, false, "latest").migrate();
        assertEquals(0, flyway(url, false, "latest").migrate().migrationsExecuted);
    }
}

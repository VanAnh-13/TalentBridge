package com.example.talentbridge;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Same scenarios as {@link FlywayMigrationTest} on a real MariaDB/MySQL server.
 * Opt-in because it needs two empty, disposable databases:
 * <pre>
 * mvn test -Dtest=MariaDbMigrationTest \
 *   -Dtest.mariadb.fresh=jdbc:mariadb://127.0.0.1:3306/tb_fresh \
 *   -Dtest.mariadb.legacy=jdbc:mariadb://127.0.0.1:3306/tb_legacy \
 *   -Dtest.mariadb.user=tb -Dtest.mariadb.password=tbpass
 * </pre>
 * WARNING: both databases are wiped (Flyway clean) before each run.
 */
@EnabledIfSystemProperty(named = "test.mariadb.fresh", matches = ".+")
class MariaDbMigrationTest {

    private static final String USER = System.getProperty("test.mariadb.user", "root");
    private static final String PASSWORD = System.getProperty("test.mariadb.password", "");

    private static Flyway flyway(String url, boolean baselineOnMigrate, String target) {
        return Flyway.configure()
                .dataSource(url, USER, PASSWORD)
                .locations("classpath:db/migration")
                .baselineOnMigrate(baselineOnMigrate)
                .baselineVersion("1")
                .cleanDisabled(false)
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
    void freshDatabase() throws Exception {
        String url = System.getProperty("test.mariadb.fresh");
        flyway(url, false, "latest").clean();
        flyway(url, false, "latest").migrate();

        try (Connection c = DriverManager.getConnection(url, USER, PASSWORD)) {
            assertEquals(3, count(c, "SELECT COUNT(*) FROM roles"));
            assertEquals(61, count(c, "SELECT COUNT(*) FROM permissions"));
            assertEquals(60, count(c, "SELECT COUNT(*) FROM roles_permissions rp JOIN roles r ON r.id = rp.role_id WHERE r.name = 'ADMIN'"));
            assertEquals(26, count(c, "SELECT COUNT(*) FROM roles_permissions rp JOIN roles r ON r.id = rp.role_id WHERE r.name = 'RECRUITER'"));
            assertEquals(21, count(c, "SELECT COUNT(*) FROM roles_permissions rp JOIN roles r ON r.id = rp.role_id WHERE r.name = 'USER'"));
            assertEquals(0, count(c, "SELECT COUNT(*) FROM users"));
        }
        assertEquals(0, flyway(url, false, "latest").migrate().migrationsExecuted, "re-run must be a no-op");
    }

    @Test
    void legacyDatabaseWithDuplicates() throws Exception {
        String url = System.getProperty("test.mariadb.legacy", System.getProperty("test.mariadb.fresh"));
        flyway(url, false, "latest").clean();

        flyway(url, false, "1").migrate();
        try (Connection c = DriverManager.getConnection(url, USER, PASSWORD); Statement s = c.createStatement()) {
            s.execute("DROP TABLE flyway_schema_history");
            s.execute("INSERT INTO roles (name, description, active) VALUES ('ADMIN','a',1),('RECRUITER','r',1),('USER','u',1)");
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

        flyway(url, true, "latest").migrate();

        try (Connection c = DriverManager.getConnection(url, USER, PASSWORD)) {
            assertEquals(3, count(c, "SELECT COUNT(*) FROM roles"));
            assertEquals(61, count(c, "SELECT COUNT(*) FROM permissions"));
            assertEquals(60, count(c, "SELECT COUNT(*) FROM roles_permissions rp JOIN roles r ON r.id = rp.role_id WHERE r.name = 'ADMIN'"));
            assertEquals(0, count(c, "SELECT COUNT(*) FROM roles_permissions WHERE permission_id NOT IN (SELECT id FROM permissions)"));
            // V4: the old seed let recruiters modify any company's data; that grant must be gone
            assertEquals(0, count(c, "SELECT COUNT(*) FROM roles_permissions rp JOIN roles r ON r.id = rp.role_id JOIN permissions p ON p.id = rp.permission_id "
                    + "WHERE r.name = 'RECRUITER' AND ((p.method = 'PUT' AND p.api_path = '/jobs/{id}') OR (p.method = 'DELETE' AND p.api_path = '/companies/{id}'))"));
            assertEquals(1, count(c, "SELECT COUNT(*) FROM roles_permissions rp JOIN roles r ON r.id = rp.role_id JOIN permissions p ON p.id = rp.permission_id "
                    + "WHERE r.name = 'RECRUITER' AND p.method = 'PUT' AND p.api_path = '/jobs/company/{id}'"));
            try (Statement s = c.createStatement()) {
                assertThrows(SQLException.class, () -> s.execute(
                        "INSERT INTO permissions (name, api_path, method, module) VALUES ('Dup','/roles','POST','ROLE')"));
            }
        }
    }
}

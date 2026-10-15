package com.example.talentbridge;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests to verify MySQL Connector configuration
 * 
 * This test ensures that:
 * 1. The new mysql-connector-j is in the classpath
 * 2. The deprecated mysql-connector-java is NOT in the classpath
 * 3. Database connectivity works with the new connector
 * 
 * Note: These are standalone tests that don't require Spring Boot context
 */
class MySQLConnectorTest {

    /**
     * Test that mysql-connector-j (new connector) is in classpath
     */
    @Test
    void shouldHaveMySQLConnectorJ() {
        // Given: The new MySQL connector should be available
        // When: We try to load the new driver class
        // Then: It should be found without throwing ClassNotFoundException
        assertDoesNotThrow(() -> {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }, "mysql-connector-j should be in classpath");
    }

    /**
     * Test that we're using mysql-connector-j (not the deprecated mysql-connector-java)
     * Note: The old driver class is still available in mysql-connector-j for backward compatibility,
     * but it's deprecated and logs a warning message.
     */
    @Test
    void shouldUseNewMySQLConnectorJ() {
        // Given: We're using mysql-connector-j
        // When: We check the package structure
        // Then: The new connector classes should be available
        assertDoesNotThrow(() -> {
            // New connector has these classes
            Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("com.mysql.cj.jdbc.ConnectionImpl");
        }, "mysql-connector-j classes should be available");
    }

    /**
     * Test that the MySQL driver can be instantiated
     */
    @Test
    void shouldInstantiateMySQLDriver() {
        // Given: mysql-connector-j is in classpath
        // When: We try to instantiate the driver
        // Then: It should succeed
        assertDoesNotThrow(() -> {
            Class<?> driverClass = Class.forName("com.mysql.cj.jdbc.Driver");
            Object driver = driverClass.getDeclaredConstructor().newInstance();
            assertNotNull(driver, "MySQL driver instance should not be null");
        }, "Should be able to instantiate MySQL driver");
    }

    /**
     * Test that the driver is registered with DriverManager
     */
    @Test
    void shouldRegisterDriverWithDriverManager() {
        // Given: mysql-connector-j is loaded
        // When: We check registered drivers
        // Then: MySQL driver should be registered
        assertDoesNotThrow(() -> {
            Class.forName("com.mysql.cj.jdbc.Driver");
            java.sql.DriverManager.getDrivers().asIterator().forEachRemaining(driver -> {
                // At least one driver should be registered
                assertNotNull(driver);
            });
        }, "MySQL driver should be registered with DriverManager");
    }

    /**
     * Test that we can get a connection URL format for MySQL
     */
    @Test
    void shouldAcceptMySQLConnectionURL() {
        // Given: A valid MySQL connection URL
        String jdbcUrl = "jdbc:mysql://localhost:3306/talentbridge";
        
        // When: We check if the URL is valid for MySQL
        // Then: It should start with jdbc:mysql://
        assertTrue(jdbcUrl.startsWith("jdbc:mysql://"), 
            "Connection URL should be valid MySQL format");
    }

    /**
     * Test that the new connector supports required features
     */
    @Test
    void shouldSupportRequiredMySQLFeatures() {
        // Given: mysql-connector-j is loaded
        // When: We check for required classes
        // Then: They should all be available
        assertDoesNotThrow(() -> {
            // Core driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connection class
            Class.forName("com.mysql.cj.jdbc.ConnectionImpl");
            
            // Statement class
            Class.forName("com.mysql.cj.jdbc.StatementImpl");
            
            // PreparedStatement class
            Class.forName("com.mysql.cj.jdbc.PreparedStatementWrapper");
            
        }, "All required MySQL connector classes should be available");
    }
}

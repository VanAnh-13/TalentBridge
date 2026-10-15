-- TalentBridge Database Initialization Script
-- This script runs automatically when the database is first created

-- Create database if not exists (for manual setup)
-- CREATE DATABASE IF NOT EXISTS talentbridge CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE talentbridge;

-- Set timezone
SET time_zone = '+07:00';

-- Enable event scheduler for cleanup tasks
SET GLOBAL event_scheduler = ON;

-- Create event to clean up old sessions (optional)
DELIMITER $$
CREATE EVENT IF NOT EXISTS cleanup_old_sessions
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
BEGIN
  -- This can be used to clean up old data if needed
  -- Example: DELETE FROM sessions WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY);
  SELECT 'Session cleanup event created' AS message;
END$$
DELIMITER ;

-- Performance optimization settings
SET GLOBAL innodb_buffer_pool_size = 268435456; -- 256MB
SET GLOBAL max_connections = 200;
SET GLOBAL connect_timeout = 10;
SET GLOBAL wait_timeout = 600;
SET GLOBAL interactive_timeout = 600;

-- Enable slow query log for monitoring
SET GLOBAL slow_query_log = 1;
SET GLOBAL long_query_time = 2;

-- Character set configuration
ALTER DATABASE talentbridge CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Display initialization message
SELECT 
    'TalentBridge database initialized successfully' AS message,
    NOW() AS timestamp,
    DATABASE() AS database_name;

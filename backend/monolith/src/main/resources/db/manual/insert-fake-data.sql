-- =====================================================
-- MASTER SCRIPT TO INSERT ALL FAKE DATA
-- Run this file to insert 100+ records into TalentBridge
-- Total: 130+ records across all tables
-- =====================================================

-- Execute all parts in order
SOURCE fake-data-part1.sql;  -- Skills (25) & Companies (20)
SOURCE fake-data-part2.sql;  -- Users (30)
SOURCE fake-data-part3.sql;  -- Jobs (25)
SOURCE fake-data-part4.sql;  -- Job-Skills & Resumes (30)

-- =====================================================
-- Alternative: Run as a single transaction
-- Uncomment below if you want to run everything at once
-- =====================================================
/*
START TRANSACTION;

-- Include content from all parts here
-- (Copy paste content from fake-data-part1.sql to fake-data-part4.sql)

COMMIT;
*/

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================
SELECT 'Data insertion completed!' AS Status;
SELECT 'Skills count:' AS Table_Name, COUNT(*) AS Record_Count FROM skills
UNION ALL
SELECT 'Companies count:', COUNT(*) FROM companies  
UNION ALL
SELECT 'Users count:', COUNT(*) FROM users
UNION ALL
SELECT 'Jobs count:', COUNT(*) FROM jobs
UNION ALL
SELECT 'Resumes count:', COUNT(*) FROM resumes;

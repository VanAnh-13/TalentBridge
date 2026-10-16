# Loading sample (fake) data

These scripts are **not** run by Flyway; load them by hand into a development database. They add 130+ realistic
records for TalentBridge:

- **25 skills**: Java, React, Python, Docker, ...
- **20 companies**: FPT, VNG, Grab, Shopee, Tiki, MoMo, ...
- **30 users**: 10 recruiters and 20 job seekers
- **25 jobs**: from fresher to senior, across IT roles
- **30 applications (resumes)**: in the states PENDING, REVIEWING, APPROVED and REJECTED

People, street and district names are real-world Vietnamese names and are kept as they are.

## How to run

### Option 1: one part at a time
```bash
# connect to MariaDB/MySQL
mysql -u root -p talentbridge

# then run each part
source /path/to/fake-data-part1.sql;  # skills & companies
source /path/to/fake-data-part2.sql;  # users
source /path/to/fake-data-part3.sql;  # jobs
source /path/to/fake-data-part4.sql;  # job skills & applications
```

### Option 2: the master file
```bash
mysql -u root -p talentbridge < insert-fake-data.sql
```

## Accounts

Every sample user has the password `password123`.

| Role | Email |
|------|-------|
| Admin | `admin@talentbridge.vn` |
| Recruiter | `hr.fpt@fpt.com.vn` |
| Job seeker | `nguyenvan@gmail.com` |

## What the data contains

### Notable companies
- **FPT Software**: Vietnam's largest software company
- **VNG Corporation**: owner of Zalo and ZaloPay
- **Grab, Shopee, Tiki**: big tech companies
- **MoMo**: Vietnam's number one e-wallet
- **VinAI**: AI research institute

### Jobs
- **Level**: INTERN, FRESHER, MIDDLE (2-4 years), SENIOR (5+ years)
- **Salary**: 5 to 40 million VND
- **Roles**: backend, frontend, full-stack, DevOps, AI/ML, mobile
- **Technologies**: Java, Python, JavaScript, React, Node.js, .NET, PHP

### Application states
- **PENDING**: waiting for review
- **REVIEWING**: being reviewed
- **APPROVED**: accepted
- **REJECTED**: rejected

## Checking the data

```sql
-- count the records
SELECT 'Skills' AS Table_Name, COUNT(*) AS Count FROM skills
UNION ALL
SELECT 'Companies', COUNT(*) FROM companies
UNION ALL
SELECT 'Users', COUNT(*) FROM users
UNION ALL
SELECT 'Jobs', COUNT(*) FROM jobs
UNION ALL
SELECT 'Resumes', COUNT(*) FROM resumes;

-- some companies
SELECT id, name, address FROM companies LIMIT 10;

-- open jobs
SELECT j.name, c.name AS company, j.salary, j.level
FROM jobs j
JOIN companies c ON j.company_id = c.id
WHERE j.active = true
LIMIT 10;

-- applications by state
SELECT status, COUNT(*) AS count
FROM resumes
GROUP BY status;
```

## Notes
- The data is modelled on the Vietnamese IT job market (companies and jobs based on real ones).
- Salaries follow 2024-2025 market surveys.
- Skills are the ones most common in the industry today.

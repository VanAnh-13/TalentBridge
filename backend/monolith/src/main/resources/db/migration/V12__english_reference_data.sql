-- V12: the built-in roles' descriptions in English. Only descriptions still holding the original V2 text change;
-- a description an administrator has edited is left alone. (V2 keeps its original text: applied migrations are
-- never edited, their checksums are verified on start-up.)

UPDATE roles SET description = 'System administration'
WHERE name = 'ADMIN' AND description = 'Quyền quản trị hệ thống';

UPDATE roles SET description = 'Recruiter - manages job postings and applications'
WHERE name = 'RECRUITER' AND description = 'Nhà tuyển dụng - Quản lý tin tuyển dụng và hồ sơ ứng viên';

UPDATE roles SET description = 'Candidate - applies for jobs'
WHERE name = 'USER' AND description = 'Người dùng - Ứng tuyển việc làm';

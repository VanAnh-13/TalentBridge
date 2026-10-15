-- =====================================================
-- INSERT COMPANY LOGOS WITH PUBLIC IMAGE URLs
-- Using real logos from public sources
-- =====================================================

-- Insert logos for existing companies
-- Using ON DUPLICATE KEY UPDATE to avoid conflicts

-- FPT Software (ID 1)
INSERT INTO company_logos (company_id, logo_url)
VALUES (1, 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/11/FPT_logo_2010.svg/1200px-FPT_logo_2010.svg.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- VNG Corporation (ID 2)
INSERT INTO company_logos (company_id, logo_url)
VALUES (2, 'https://upload.wikimedia.org/wikipedia/vi/thumb/6/61/VNG_Corporation_logo.svg/1200px-VNG_Corporation_logo.svg.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- Viettel Software (ID 3)
INSERT INTO company_logos (company_id, logo_url)
VALUES (3, 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/Logo_Viettel.svg/1200px-Logo_Viettel.svg.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- TMA Solutions (ID 4)
INSERT INTO company_logos (company_id, logo_url)
VALUES (4, 'https://www.tma.vn/Content/images/logo-tma-tma.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- KMS Technology (ID 5)
INSERT INTO company_logos (company_id, logo_url)
VALUES (5, 'https://kms-technology.com/wp-content/uploads/2018/10/logo.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- Sendo (ID 6)
INSERT INTO company_logos (company_id, logo_url)
VALUES (6, 'https://media.loveitopcdn.com/3807/logo-sendo-dongphucsongphu.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- Grab Vietnam (ID 7)  
INSERT INTO company_logos (company_id, logo_url)
VALUES (7, 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/Grab_Logo_2021.svg/1200px-Grab_Logo_2021.svg.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- Shopee Vietnam (ID 8)
INSERT INTO company_logos (company_id, logo_url)
VALUES (8, 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fe/Shopee.svg/1200px-Shopee.svg.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- Tiki (ID 9)
INSERT INTO company_logos (company_id, logo_url)
VALUES (9, 'https://salt.tikicdn.com/ts/upload/e4/49/6c/270be9859abd5f5ec5071da65fab0a94.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- MoMo (ID 10)
INSERT INTO company_logos (company_id, logo_url)
VALUES (10, 'https://upload.wikimedia.org/wikipedia/vi/f/fe/MoMo_Logo.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- VinAI (ID 11)
INSERT INTO company_logos (company_id, logo_url)
VALUES (11, 'https://www.vinai.io/wp-content/themes/vinai/assets/images/logo.svg')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- MISA (ID 12)
INSERT INTO company_logos (company_id, logo_url)
VALUES (12, 'https://www.misa.vn/Content/Images/logo-misa-2021.svg')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- NashTech (ID 13)
INSERT INTO company_logos (company_id, logo_url)
VALUES (13, 'https://nashtechglobal.com/media/t3lbpgk0/logo-nashtech-primary.svg')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- Zalo (ID 14)
INSERT INTO company_logos (company_id, logo_url)
VALUES (14, 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/Zalo_logo_2019.svg/1200px-Zalo_logo_2019.svg.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- VPBank (ID 15)
INSERT INTO company_logos (company_id, logo_url)
VALUES (15, 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/VP_Bank_logo.svg/1200px-VP_Bank_logo.svg.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- Lazada Vietnam (ID 16)
INSERT INTO company_logos (company_id, logo_url)
VALUES (16, 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/Lazada.svg/1200px-Lazada.svg.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- Techcombank (ID 17)
INSERT INTO company_logos (company_id, logo_url)
VALUES (17, 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/Logo_ngan_hang_Techcombank.svg/1200px-Logo_ngan_hang_Techcombank.svg.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- Base.vn (ID 18)
INSERT INTO company_logos (company_id, logo_url)
VALUES (18, 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/base-vn-5dc92de816e21.jpg')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- NFQ Asia (ID 19)
INSERT INTO company_logos (company_id, logo_url)
VALUES (19, 'https://www.nfq.asia/assets/images/common/header-logo.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- Sky Mavis (ID 20)
INSERT INTO company_logos (company_id, logo_url)
VALUES (20, 'https://cdn.axieinfinity.com/landing-page/_next/static/images/sky-mavis-e8de0a9b3c9f982fc79c039b96dcc2e7.png')
ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);

-- =====================================================
-- VERIFY INSERTS
-- =====================================================
SELECT c.id, c.name, cl.logo_url
FROM companies c
LEFT JOIN company_logos cl ON c.id = cl.company_id
ORDER BY c.id;

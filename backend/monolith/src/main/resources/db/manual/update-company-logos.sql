-- =====================================================
-- UPDATE COMPANY LOGOS WITH PUBLIC IMAGE URLs
-- Using real logos from public sources
-- =====================================================

-- FPT Software
UPDATE companies 
SET logo_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/11/FPT_logo_2010.svg/1200px-FPT_logo_2010.svg.png',
    updated_at = NOW()
WHERE name = 'FPT Software';

-- VNG Corporation  
UPDATE companies
SET logo_url = 'https://upload.wikimedia.org/wikipedia/vi/thumb/6/61/VNG_Corporation_logo.svg/1200px-VNG_Corporation_logo.svg.png',
    updated_at = NOW()
WHERE name = 'VNG Corporation';

-- Viettel
UPDATE companies
SET logo_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/Logo_Viettel.svg/1200px-Logo_Viettel.svg.png',
    updated_at = NOW()
WHERE name = 'Viettel Software';

-- TMA Solutions
UPDATE companies
SET logo_url = 'https://www.tma.vn/Content/images/logo-tma-tma.png',
    updated_at = NOW()
WHERE name = 'TMA Solutions';

-- KMS Technology
UPDATE companies
SET logo_url = 'https://kms-technology.com/wp-content/uploads/2018/10/logo.png',
    updated_at = NOW()
WHERE name = 'KMS Technology';

-- Sendo
UPDATE companies
SET logo_url = 'https://media.loveitopcdn.com/3807/logo-sendo-dongphucsongphu.png',
    updated_at = NOW()
WHERE name = 'Sendo';

-- Grab Vietnam
UPDATE companies
SET logo_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/Grab_Logo_2021.svg/1200px-Grab_Logo_2021.svg.png',
    updated_at = NOW()
WHERE name = 'Grab Vietnam';

-- Shopee Vietnam
UPDATE companies
SET logo_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fe/Shopee.svg/1200px-Shopee.svg.png',
    updated_at = NOW()
WHERE name = 'Shopee Vietnam';

-- Tiki
UPDATE companies
SET logo_url = 'https://salt.tikicdn.com/ts/upload/e4/49/6c/270be9859abd5f5ec5071da65fab0a94.png',
    updated_at = NOW()
WHERE name = 'Tiki';

-- MoMo
UPDATE companies
SET logo_url = 'https://upload.wikimedia.org/wikipedia/vi/f/fe/MoMo_Logo.png',
    updated_at = NOW()
WHERE name = 'MoMo';

-- VinAI
UPDATE companies
SET logo_url = 'https://www.vinai.io/wp-content/themes/vinai/assets/images/logo.svg',
    updated_at = NOW()
WHERE name = 'VinAI';

-- MISA
UPDATE companies
SET logo_url = 'https://www.misa.vn/Content/Images/logo-misa-2021.svg',
    updated_at = NOW()
WHERE name = 'MISA';

-- NashTech
UPDATE companies
SET logo_url = 'https://nashtechglobal.com/media/t3lbpgk0/logo-nashtech-primary.svg',
    updated_at = NOW()
WHERE name = 'NashTech';

-- Zalo
UPDATE companies
SET logo_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/Zalo_logo_2019.svg/1200px-Zalo_logo_2019.svg.png',
    updated_at = NOW()
WHERE name = 'Zalo';

-- VPBank
UPDATE companies
SET logo_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/VP_Bank_logo.svg/1200px-VP_Bank_logo.svg.png',
    updated_at = NOW()
WHERE name = 'VPBank';

-- Lazada Vietnam
UPDATE companies
SET logo_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/Lazada.svg/1200px-Lazada.svg.png',
    updated_at = NOW()
WHERE name = 'Lazada Vietnam';

-- Techcombank
UPDATE companies
SET logo_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/Logo_ngan_hang_Techcombank.svg/1200px-Logo_ngan_hang_Techcombank.svg.png',
    updated_at = NOW()
WHERE name = 'Techcombank';

-- Base.vn
UPDATE companies
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/base-vn-5dc92de816e21.jpg',
    updated_at = NOW()
WHERE name = 'Base.vn';

-- NFQ Asia
UPDATE companies
SET logo_url = 'https://www.nfq.asia/assets/images/common/header-logo.png',
    updated_at = NOW()
WHERE name = 'NFQ Asia';

-- Sky Mavis
UPDATE companies
SET logo_url = 'https://cdn.axieinfinity.com/landing-page/_next/static/images/sky-mavis-e8de0a9b3c9f982fc79c039b96dcc2e7.png',
    updated_at = NOW()
WHERE name = 'Sky Mavis';

-- =====================================================
-- VERIFY UPDATES
-- =====================================================
SELECT id, name, logo_url 
FROM companies 
WHERE logo_url IS NOT NULL
ORDER BY id;

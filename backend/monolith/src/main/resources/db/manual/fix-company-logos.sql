-- =====================================================
-- FIX COMPANY LOGOS WITH STABLE PUBLIC URLs
-- Using reliable CDNs and official sources
-- =====================================================

-- FPT Software
UPDATE company_logos
SET logo_url = 'https://cdn.hachium.com/vi/brand-resources/fpt-software/images/612d018c04c2a-fpt-software-logo-social.png'
WHERE company_id = 1;

-- VNG Corporation
UPDATE company_logos  
SET logo_url = 'https://statics.vnpcdn.com/v20/vng/i/logo-vng.png'
WHERE company_id = 2;

-- Viettel Software
UPDATE company_logos
SET logo_url = 'https://inkythuatso.com/uploads/thumbnails/800/2021/09/logo-viettel-inkythuatso-1-01-09-14-34-18.jpg'
WHERE company_id = 3;

-- TMA Solutions
UPDATE company_logos
SET logo_url = 'https://images.careerbuilder.vn/employer_folders/lot4/212564/110754tma-logo.png'
WHERE company_id = 4;

-- KMS Technology
UPDATE company_logos
SET logo_url = 'https://images.careerbuilder.vn/employer_folders/lot5/201945/155915kmstechnologylogo.png'
WHERE company_id = 5;

-- Sendo
UPDATE company_logos
SET logo_url = 'https://cdn.hachium.com/vi/seo-tags/sendo/images/60ba688bc8593-sendo-logo-1200x630.png'
WHERE company_id = 6;

-- Grab Vietnam
UPDATE company_logos
SET logo_url = 'https://logos-world.net/wp-content/uploads/2021/08/Grab-Logo.png'
WHERE company_id = 7;

-- Shopee Vietnam
UPDATE company_logos
SET logo_url = 'https://www.citypng.com/public/uploads/preview/hd-shopee-logo-icon-transparent-png-11640574003pbqgs1uuk0.png'
WHERE company_id = 8;

-- Tiki
UPDATE company_logos
SET logo_url = 'https://vcdn.tikicdn.com/ts/upload/0e/07/78/ee828743c9afa9792cf20d75995e134e.png'
WHERE company_id = 9;

-- MoMo
UPDATE company_logos
SET logo_url = 'https://developers.momo.vn/v3/vi/assets/images/logo-momo-png-bf8bae36c7080c7eb0833fef095e8812.png'
WHERE company_id = 10;

-- VinAI
UPDATE company_logos
SET logo_url = 'https://storage.googleapis.com/vinai/VinAI_color_transp%20(Custom).png'
WHERE company_id = 11;

-- MISA
UPDATE company_logos
SET logo_url = 'https://www.misa.vn/uploads/default/original/2X/9/9c7d37bb73c14e67c8dc3e46cdeb9d936be5da60.png'
WHERE company_id = 12;

-- NashTech
UPDATE company_logos
SET logo_url = 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBtO90pR_BcbbJC5K7NVfVJfQ-2Q8DFQo3pQ&s'
WHERE company_id = 13;

-- Zalo
UPDATE company_logos
SET logo_url = 'https://play-lh.googleusercontent.com/20sZU6YqQ3Td7oq3fn4YczApZgTLRTrC3HEc6B2bydH1PqbeXfhLzI5EJYNyKhtSYQ'
WHERE company_id = 14;

-- VPBank
UPDATE company_logos
SET logo_url = 'https://www.vpbank.com.vn/sites/default/files/Logo-VPBank.png'
WHERE company_id = 15;

-- Lazada Vietnam
UPDATE company_logos
SET logo_url = 'https://lzd-img-global.slatic.net/g/tps/tfs/TB1PApewFT7gK0jSZFpXXaTkpXa-200-200.png'
WHERE company_id = 16;

-- Techcombank
UPDATE company_logos
SET logo_url = 'https://www.techcombank.com.vn/uploads/originals/dau-tu-chung-khoan-cung-techcombank/logo.png'
WHERE company_id = 17;

-- Base.vn
UPDATE company_logos
SET logo_url = 'https://base.vn/wp-content/uploads/2021/09/logo-base.png'
WHERE company_id = 18;

-- NFQ Asia
UPDATE company_logos
SET logo_url = 'https://www.nfq.asia/assets/images/common/header-logo.png'
WHERE company_id = 19;

-- Sky Mavis
UPDATE company_logos
SET logo_url = 'https://skymavis.com/images/Sky-Mavis-logo.png'
WHERE company_id = 20;

-- =====================================================
-- VERIFY UPDATES
-- =====================================================
SELECT c.id, c.name, cl.logo_url
FROM companies c
LEFT JOIN company_logos cl ON c.id = cl.company_id
ORDER BY c.id;

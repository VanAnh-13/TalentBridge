-- =====================================================
-- UPDATE COMPANY LOGOS WITH IMPROVED PNG/JPG URLS
-- Better compatibility and quality images
-- =====================================================

-- Update logos to use PNG/JPG versions for better compatibility

-- FPT Software
UPDATE company_logos
SET logo_url = 'https://i.imgur.com/wDVjpVA.png'
WHERE company_id = 1;

-- VNG Corporation
UPDATE company_logos
SET logo_url = 'https://cdn1.vieclam24h.vn/upload/files_cua_nguoi_dung/logo/2022/09/17/vng-corporation-1663386630.png'
WHERE company_id = 2;

-- Viettel Software
UPDATE company_logos
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/viettel-61107fb4e30f0.jpg'
WHERE company_id = 3;

-- TMA Solutions
UPDATE company_logos
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/cong-ty-tnhh-tma-solutions-5d0456c752b83.jpg'
WHERE company_id = 4;

-- KMS Technology
UPDATE company_logos
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/cong-ty-tnhh-kms-technology-vietnam-5f7e8fb96a60d.jpg'
WHERE company_id = 5;

-- Grab Vietnam
UPDATE company_logos
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/grab-vietnam-5e8359a03357d.jpg'
WHERE company_id = 7;

-- Shopee Vietnam
UPDATE company_logos
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/shopee-5f06b515117d1.jpg'
WHERE company_id = 8;

-- Tiki
UPDATE company_logos
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/cong-ty-tnhh-ti-ki-6098dcfc9a13b.jpg'
WHERE company_id = 9;

-- MoMo
UPDATE company_logos
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/cong-ty-cp-dich-vu-di-dong-truc-tuyen-609879b88e33f.jpg'
WHERE company_id = 10;

-- MISA
UPDATE company_logos  
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/cong-ty-co-phan-misa-5a73e0dc37d81.jpg'
WHERE company_id = 12;

-- NashTech
UPDATE company_logos
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/nashtech-vietnam-618f4ea3c8709.jpg'
WHERE company_id = 13;

-- VPBank
UPDATE company_logos
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/ngan-hang-tmcp-viet-nam-thinh-vuong-vpbank-5ece72c7350f0.jpg'
WHERE company_id = 15;

-- Lazada Vietnam
UPDATE company_logos
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/lazada-viet-nam-6099fa4303b67.jpg'
WHERE company_id = 16;

-- Techcombank
UPDATE company_logos
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/ngan-hang-tmcp-ky-thuong-viet-nam-techcombank-5ece721f8c05f.jpg'
WHERE company_id = 17;

-- Base.vn
UPDATE company_logos
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/base-vn-5dc92de816e21.jpg'
WHERE company_id = 18;

-- NFQ Asia
UPDATE company_logos
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/nfq-asia-6074e30fe4e2e.jpg'
WHERE company_id = 19;

-- =====================================================
-- Add some generic tech logos for remaining companies
-- =====================================================

-- For VinAI (ID 11) - Keep SVG as it works well
-- Already good

-- For Zalo (ID 14)
UPDATE company_logos
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/zalo-5ff3cc16e9e09.jpg'
WHERE company_id = 14;

-- For Sky Mavis (ID 20) 
UPDATE company_logos
SET logo_url = 'https://cdn-new.topcv.vn/unsafe/150x/https://static.topcv.vn/company_logos/sky-mavis-617c087aac57f.jpg'
WHERE company_id = 20;

-- =====================================================
-- VERIFY UPDATES
-- =====================================================
SELECT c.id, c.name, cl.logo_url
FROM companies c
LEFT JOIN company_logos cl ON c.id = cl.company_id
ORDER BY c.id;

-- =====================================================
-- FINAL FIX: USE PLACEHOLDER LOGOS FROM STABLE SOURCES
-- Using placeholder images and stable CDNs
-- =====================================================

-- Use placeholder images with company initials for now
-- These are guaranteed to work

-- FPT Software
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=FPT+Software&background=0D8ABC&color=fff&size=200'
WHERE company_id = 1;

-- VNG Corporation
UPDATE company_logos  
SET logo_url = 'https://ui-avatars.com/api/?name=VNG&background=FF6B00&color=fff&size=200'
WHERE company_id = 2;

-- Viettel Software
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=Viettel&background=ED1C24&color=fff&size=200'
WHERE company_id = 3;

-- TMA Solutions
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=TMA&background=005EB8&color=fff&size=200'
WHERE company_id = 4;

-- KMS Technology
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=KMS&background=00B4D8&color=fff&size=200'
WHERE company_id = 5;

-- Sendo
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=Sendo&background=EE4D2D&color=fff&size=200'
WHERE company_id = 6;

-- Grab Vietnam
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=Grab&background=00B14F&color=fff&size=200'
WHERE company_id = 7;

-- Shopee Vietnam
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=Shopee&background=FF6633&color=fff&size=200'
WHERE company_id = 8;

-- Tiki
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=Tiki&background=1A94FF&color=fff&size=200'
WHERE company_id = 9;

-- MoMo
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=MoMo&background=D82D8B&color=fff&size=200'
WHERE company_id = 10;

-- VinAI
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=VinAI&background=C41230&color=fff&size=200'
WHERE company_id = 11;

-- MISA
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=MISA&background=0066CC&color=fff&size=200'
WHERE company_id = 12;

-- NashTech
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=NashTech&background=FF5722&color=fff&size=200'
WHERE company_id = 13;

-- Zalo
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=Zalo&background=0068FF&color=fff&size=200'
WHERE company_id = 14;

-- VPBank
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=VPBank&background=00953B&color=fff&size=200'
WHERE company_id = 15;

-- Lazada Vietnam
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=Lazada&background=F57224&color=fff&size=200'
WHERE company_id = 16;

-- Techcombank
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=TCB&background=DA251C&color=fff&size=200'
WHERE company_id = 17;

-- Base.vn
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=Base&background=4CAF50&color=fff&size=200'
WHERE company_id = 18;

-- NFQ Asia
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=NFQ&background=673AB7&color=fff&size=200'
WHERE company_id = 19;

-- Sky Mavis
UPDATE company_logos
SET logo_url = 'https://ui-avatars.com/api/?name=Sky+Mavis&background=7C4DFF&color=fff&size=200'
WHERE company_id = 20;

-- =====================================================
-- VERIFY UPDATES
-- =====================================================
SELECT c.id, c.name, cl.logo_url
FROM companies c
LEFT JOIN company_logos cl ON c.id = cl.company_id
ORDER BY c.id;

-- V3: data.sql used to re-insert every permission on each startup (spring.sql.init.mode=always) and the
-- table had no unique key, so deployed databases accumulated duplicate permission rows.
-- 1) re-point role assignments from duplicates to the surviving (lowest id) permission,
-- 2) drop assignments and rows of the duplicates, 3) forbid duplicates from now on.

INSERT INTO roles_permissions (role_id, permission_id)
SELECT DISTINCT rp.role_id, k.keep_id
FROM roles_permissions rp
JOIN permissions p ON p.id = rp.permission_id
JOIN (SELECT api_path, method, MIN(id) AS keep_id FROM permissions GROUP BY api_path, method) k
  ON k.api_path = p.api_path AND k.method = p.method
WHERE NOT EXISTS (SELECT 1 FROM roles_permissions x WHERE x.role_id = rp.role_id AND x.permission_id = k.keep_id);

DELETE FROM roles_permissions
WHERE permission_id NOT IN (SELECT keep_id FROM (SELECT MIN(id) AS keep_id FROM permissions GROUP BY api_path, method) k);

DELETE FROM permissions
WHERE id NOT IN (SELECT keep_id FROM (SELECT MIN(id) AS keep_id FROM permissions GROUP BY api_path, method) k);

ALTER TABLE permissions ADD CONSTRAINT uk_permissions_api_path_method UNIQUE (api_path, method);

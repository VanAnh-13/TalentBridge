-- In-app notifications (inbox) and the permissions to read them.

create table notifications (
  id bigint not null auto_increment,
  user_id bigint not null,
  type varchar(50) not null,
  title varchar(255) not null,
  body varchar(1000),
  link varchar(255),
  is_read bit not null,
  created_at datetime(6) not null,
  primary key (id)
) engine=InnoDB;

create index idx_notifications_user on notifications (user_id, is_read, created_at);
alter table notifications add constraint fk_notifications_user foreign key (user_id) references users (id) on delete cascade;

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /notifications', '/notifications', 'GET', 'NOTIFICATION', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/notifications' AND x.method = 'GET');
INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'GET /notifications/unread-count', '/notifications/unread-count', 'GET', 'NOTIFICATION', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/notifications/unread-count' AND x.method = 'GET');
INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'PATCH /notifications/{id}/read', '/notifications/{id}/read', 'PATCH', 'NOTIFICATION', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/notifications/{id}/read' AND x.method = 'PATCH');
INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT 'POST /notifications/read-all', '/notifications/read-all', 'POST', 'NOTIFICATION', NOW(), NOW(), 'system', 'system' FROM (SELECT 1 AS x) t
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = '/notifications/read-all' AND x.method = 'POST');

-- every signed-in role reads its own inbox; ADMIN keeps the whole API catalog
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.module = 'NOTIFICATION'
WHERE r.name IN ('ADMIN', 'RECRUITER', 'USER')
  AND NOT EXISTS (SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

-- Interview scheduling: a recruiter proposes slots for an application, the candidate confirms one.

create table interviews (
  id bigint not null auto_increment,
  resume_id bigint not null,
  company_id bigint not null,
  recruiter_id bigint not null,
  candidate_id bigint not null,
  mode varchar(20) not null,
  location varchar(500) not null,
  note varchar(1000),
  duration_minutes integer not null,
  confirmed_slot datetime(6),
  status varchar(20) not null,
  reminded_24h bit not null,
  reminded_1h bit not null,
  created_at datetime(6) not null,
  primary key (id)
) engine=InnoDB;

create table interview_slots (
  interview_id bigint not null,
  slot datetime(6) not null
) engine=InnoDB;

create index idx_interviews_resume on interviews (resume_id, status);
create index idx_interviews_candidate on interviews (candidate_id, created_at);
create index idx_interviews_company on interviews (company_id, created_at);
create index idx_interviews_reminders on interviews (status, confirmed_slot);
create index idx_interview_slots on interview_slots (interview_id);

-- an interview is meaningless without its application, company or people
alter table interviews add constraint fk_interviews_resume foreign key (resume_id) references resumes (id) on delete cascade;
alter table interviews add constraint fk_interviews_company foreign key (company_id) references companies (id) on delete cascade;
alter table interviews add constraint fk_interviews_recruiter foreign key (recruiter_id) references users (id) on delete cascade;
alter table interviews add constraint fk_interviews_candidate foreign key (candidate_id) references users (id) on delete cascade;
alter table interview_slots add constraint fk_interview_slots_interview foreign key (interview_id) references interviews (id) on delete cascade;

-- permissions (module INTERVIEW)
INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT CONCAT(v.method, ' ', v.path), v.path, v.method, 'INTERVIEW', NOW(), NOW(), 'system', 'system'
FROM (SELECT 'POST' AS method, '/interviews' AS path
      UNION ALL SELECT 'PUT', '/interviews/{id}/reschedule'
      UNION ALL SELECT 'PUT', '/interviews/{id}/cancel'
      UNION ALL SELECT 'PUT', '/interviews/{id}/confirm'
      UNION ALL SELECT 'PUT', '/interviews/{id}/decline'
      UNION ALL SELECT 'GET', '/interviews/me'
      UNION ALL SELECT 'GET', '/interviews/company'
      UNION ALL SELECT 'GET', '/interviews/{id}/calendar.ics') v
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = v.path AND x.method = v.method);

-- ADMIN keeps the whole API catalog; recruiters organise, candidates answer, both can export the calendar
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.module = 'INTERVIEW'
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p
  ON (p.method, p.api_path) IN (
    ('POST', '/interviews'), ('PUT', '/interviews/{id}/reschedule'), ('PUT', '/interviews/{id}/cancel'),
    ('GET', '/interviews/company'), ('GET', '/interviews/{id}/calendar.ics'))
WHERE r.name = 'RECRUITER'
  AND NOT EXISTS (SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p
  ON (p.method, p.api_path) IN (
    ('PUT', '/interviews/{id}/confirm'), ('PUT', '/interviews/{id}/decline'),
    ('GET', '/interviews/me'), ('GET', '/interviews/{id}/calendar.ics'))
WHERE r.name = 'USER'
  AND NOT EXISTS (SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

-- AI assessment of how well an application's CV fits its job (one current result per application).

create table match_results (
  resume_id bigint not null,
  job_id bigint not null,
  candidate_id bigint not null,
  score int not null,
  matched_skills varchar(4000) not null,
  missing_skills varchar(4000) not null,
  summary varchar(1000) not null,
  model varchar(100) not null,
  scored_at datetime(6) not null,
  primary key (resume_id)
) engine=InnoDB;

create index idx_match_results_job on match_results (job_id, score);

alter table match_results add constraint fk_match_results_resume foreign key (resume_id) references resumes (id) on delete cascade;
alter table match_results add constraint fk_match_results_job foreign key (job_id) references jobs (id) on delete cascade;
alter table match_results add constraint fk_match_results_candidate foreign key (candidate_id) references users (id) on delete cascade;

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT CONCAT(v.method, ' ', v.path), v.path, v.method, 'MATCHING', NOW(), NOW(), 'system', 'system'
FROM (SELECT 'GET' AS method, '/matching/status' AS path
      UNION ALL SELECT 'GET', '/matching/resumes/{resumeId}'
      UNION ALL SELECT 'POST', '/matching/resumes/{resumeId}/recompute'
      UNION ALL SELECT 'GET', '/matching/jobs/{jobId}/candidates'
      UNION ALL SELECT 'GET', '/matching/me/recommendations') v
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = v.path AND x.method = v.method);

-- ADMIN keeps the whole API catalog; recruiters judge applicants, candidates get job recommendations
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.module = 'MATCHING'
WHERE (r.name = 'ADMIN'
       OR (r.name = 'RECRUITER' AND p.api_path <> '/matching/me/recommendations')
       OR (r.name = 'USER' AND p.api_path = '/matching/me/recommendations'))
  AND NOT EXISTS (SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

-- Recruiter <-> candidate chat: one conversation per application.

create table conversations (
  id bigint not null auto_increment,
  resume_id bigint not null,
  company_id bigint not null,
  candidate_id bigint not null,
  created_at datetime(6) not null,
  last_message_at datetime(6) not null,
  primary key (id),
  constraint uk_conversations_resume unique (resume_id)
) engine=InnoDB;

create table chat_messages (
  id bigint not null auto_increment,
  conversation_id bigint not null,
  sender_id bigint not null,
  text varchar(2000) not null,
  sent_at datetime(6) not null,
  primary key (id)
) engine=InnoDB;

create table conversation_reads (
  conversation_id bigint not null,
  user_id bigint not null,
  last_read_message_id bigint not null,
  primary key (conversation_id, user_id)
) engine=InnoDB;

create index idx_conversations_candidate on conversations (candidate_id, last_message_at);
create index idx_conversations_company on conversations (company_id, last_message_at);
create index idx_chat_messages_conversation on chat_messages (conversation_id, id);

alter table conversations add constraint fk_conversations_resume foreign key (resume_id) references resumes (id) on delete cascade;
alter table conversations add constraint fk_conversations_company foreign key (company_id) references companies (id) on delete cascade;
alter table conversations add constraint fk_conversations_candidate foreign key (candidate_id) references users (id) on delete cascade;
alter table chat_messages add constraint fk_chat_messages_conversation foreign key (conversation_id) references conversations (id) on delete cascade;
alter table chat_messages add constraint fk_chat_messages_sender foreign key (sender_id) references users (id) on delete cascade;
alter table conversation_reads add constraint fk_conversation_reads_conversation foreign key (conversation_id) references conversations (id) on delete cascade;
alter table conversation_reads add constraint fk_conversation_reads_user foreign key (user_id) references users (id) on delete cascade;

INSERT INTO permissions (name, api_path, method, module, created_at, updated_at, created_by, modified_by)
SELECT CONCAT(v.method, ' ', v.path), v.path, v.method, 'CHAT', NOW(), NOW(), 'system', 'system'
FROM (SELECT 'POST' AS method, '/chats/resumes/{resumeId}' AS path
      UNION ALL SELECT 'GET', '/chats'
      UNION ALL SELECT 'GET', '/chats/unread-count'
      UNION ALL SELECT 'GET', '/chats/{id}/messages'
      UNION ALL SELECT 'POST', '/chats/{id}/messages'
      UNION ALL SELECT 'POST', '/chats/{id}/read') v
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.api_path = v.path AND x.method = v.method);

-- candidates and recruiters chat; ADMIN keeps the whole API catalog
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.module = 'CHAT'
WHERE r.name IN ('ADMIN', 'RECRUITER', 'USER')
  AND NOT EXISTS (SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

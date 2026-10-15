-- V1: baseline schema (mirrors the JPA entities as created by Hibernate ddl-auto on MySQL/MariaDB).
-- Existing databases are baselined at this version (spring.flyway.baseline-on-migrate) and skip it.

create table companies (created_at datetime(6), id bigint not null auto_increment, owner_id bigint, updated_at datetime(6), address varchar(255), created_by varchar(255), description MEDIUMTEXT, modified_by varchar(255), name varchar(255), primary key (id)) engine=InnoDB;
create table company_logos (company_id bigint not null, logo_url varchar(255), primary key (company_id)) engine=InnoDB;
create table job_skill (job_id bigint not null, skill_id bigint not null) engine=InnoDB;
create table jobs (active bit, quantity integer, salary float(53), company_id bigint, created_at datetime(6), end_date datetime(6), id bigint not null auto_increment, start_date datetime(6), updated_at datetime(6), created_by varchar(255), description MEDIUMTEXT, location varchar(255), modified_by varchar(255), name varchar(255), level enum ('FRESHER','INTERN','MIDDLE','SENIOR'), primary key (id)) engine=InnoDB;
create table password_reset_tokens (used bit not null, created_at datetime(6) not null, expiry_date datetime(6) not null, id bigint not null auto_increment, user_id bigint not null, token_hash varchar(255) not null, primary key (id)) engine=InnoDB;
create table permissions (created_at datetime(6), id bigint not null auto_increment, updated_at datetime(6), api_path varchar(255) not null, created_by varchar(255), method varchar(255) not null, modified_by varchar(255), module varchar(255) not null, name varchar(255) not null, primary key (id)) engine=InnoDB;
create table resumes (created_at datetime(6), id bigint not null auto_increment, job_id bigint, updated_at datetime(6), user_id bigint, version bigint, created_by varchar(255), email varchar(255), file_key varchar(255), modified_by varchar(255), status enum ('APPROVED','PENDING','REJECTED','REVIEWING'), primary key (id)) engine=InnoDB;
create table roles (active bit not null, created_at datetime(6), id bigint not null auto_increment, updated_at datetime(6), created_by varchar(255), description varchar(255) not null, modified_by varchar(255), name varchar(255) not null, primary key (id)) engine=InnoDB;
create table roles_permissions (permission_id bigint not null, role_id bigint not null, primary key (permission_id, role_id)) engine=InnoDB;
create table skills (created_at datetime(6), id bigint not null auto_increment, updated_at datetime(6), created_by varchar(255), modified_by varchar(255), name varchar(255), primary key (id)) engine=InnoDB;
create table subscriber_skills (skill_id bigint not null, subscriber_id bigint not null) engine=InnoDB;
create table subscribers (id bigint not null auto_increment, email varchar(255), primary key (id)) engine=InnoDB;
create table users (dob date, company_id bigint, created_at datetime(6), id bigint not null auto_increment, role_id bigint, updated_at datetime(6), address varchar(255), created_by varchar(255), email varchar(255) not null, logo_url varchar(255), modified_by varchar(255), name varchar(255) not null, password varchar(255) not null, gender enum ('FEMALE','MALE','OTHER'), primary key (id)) engine=InnoDB;
alter table companies add constraint UK2xrywqtkk3tty82u66fs3s7gm unique (owner_id);
alter table password_reset_tokens add constraint UKajre85ybxavf1tt4omkrs5p6g unique (token_hash);
alter table roles add constraint UKofx66keruapi6vyqpv6f2or37 unique (name);
alter table skills add constraint UK85woe63nu9klkk9fa73vf0jd0 unique (name);
alter table users add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);
alter table companies add constraint FKikhsc7na5r394c8jghny8b6t4 foreign key (owner_id) references users (id);
alter table company_logos add constraint FKbgn3lugrtbscf0x68i3tdsme1 foreign key (company_id) references companies (id);
alter table job_skill add constraint FKdh76859joo68p6dbj9erh4pbs foreign key (skill_id) references skills (id);
alter table job_skill add constraint FKje4q8ajxb3v5bel11dhbxrb8d foreign key (job_id) references jobs (id);
alter table jobs add constraint FKrtmqcrktb6s7xq8djbs2a2war foreign key (company_id) references companies (id);
alter table password_reset_tokens add constraint FKk3ndxg5xp6v7wd4gjyusp15gq foreign key (user_id) references users (id);
alter table resumes add constraint FKjdec9qbp2blbpag6obwf0fmbd foreign key (job_id) references jobs (id);
alter table resumes add constraint FK340nuaivxiy99hslr3sdydfvv foreign key (user_id) references users (id);
alter table roles_permissions add constraint FKbx9r9uw77p58gsq4mus0mec0o foreign key (permission_id) references permissions (id);
alter table roles_permissions add constraint FKqi9odri6c1o81vjox54eedwyh foreign key (role_id) references roles (id);
alter table subscriber_skills add constraint FKhxf6u96wfokxsw8ygf3e9lqiv foreign key (skill_id) references skills (id);
alter table subscriber_skills add constraint FKnqlrhhuad8ep5frnpvtq70kg1 foreign key (subscriber_id) references subscribers (id);
alter table users add constraint FKin8gn4o1hpiwe6qe4ey7ykwq7 foreign key (company_id) references companies (id);
alter table users add constraint FKp56c1712k691lhsyewcssf40f foreign key (role_id) references roles (id);

-- The notification service's own database. user_id refers to a user of the auth service: no foreign key across
-- services. event_id makes handling an event idempotent (Kafka delivers at least once).

create table notifications (
  id bigint not null auto_increment,
  event_id varchar(36),
  user_id bigint not null,
  type varchar(50) not null,
  title varchar(255) not null,
  body varchar(1000),
  link varchar(255),
  is_read bit not null,
  created_at datetime(6) not null,
  primary key (id),
  constraint uk_notifications_event unique (event_id)
) engine=InnoDB;

create index idx_notifications_user on notifications (user_id, is_read, created_at);

-- Transactional outbox: events are written in the same transaction as the change they describe and shipped to
-- Kafka afterwards by the relay (at least once; consumers drop duplicates by event_id).

create table outbox_events (
  id bigint not null auto_increment,
  event_id varchar(36) not null,
  topic varchar(200) not null,
  event_key varchar(200),
  event_type varchar(100) not null,
  payload varchar(8000) not null,
  created_at datetime(6) not null,
  published_at datetime(6),
  attempts int not null,
  last_error varchar(500),
  primary key (id),
  constraint uk_outbox_events_event unique (event_id)
) engine=InnoDB;

create index idx_outbox_events_pending on outbox_events (published_at, id);

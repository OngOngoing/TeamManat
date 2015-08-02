# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table comment (
  id                        bigint not null,
  user_id                   bigint,
  project_id                bigint,
  comment                   varchar(255),
  constraint pk_comment primary key (id))
;

create table groups (
  id                        bigint not null,
  user_id                   bigint,
  project_id                bigint,
  constraint pk_groups primary key (id))
;

create table image (
  id                        bigint not null,
  project_id                bigint,
  data                      blob,
  img_type                  integer,
  constraint pk_image primary key (id))
;

create table inbox_message (
  id                        bigint not null,
  sender_id                 bigint,
  receiver_id               bigint,
  comment_id                bigint,
  is_read                   integer,
  timestamp                 timestamp not null,
  constraint pk_inbox_message primary key (id))
;

create table project (
  id                        bigint not null,
  project_name              varchar(255),
  project_description       varchar(255),
  constraint pk_project primary key (id))
;

create table rate (
  id                        bigint not null,
  score                     integer,
  user_id                   bigint,
  project_id                bigint,
  criteria_id               bigint,
  constraint pk_rate primary key (id))
;

create table rate_criterion (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  constraint pk_rate_criterion primary key (id))
;

create table setting (
  id                        bigint not null,
  key_name                  varchar(255),
  key_value                 varchar(255),
  id_type                   integer,
  description               varchar(255),
  constraint uq_setting_key_name unique (key_name),
  constraint pk_setting primary key (id))
;

create table user_account (
  id                        bigint not null,
  username                  varchar(255) not null,
  password                  varchar(255) not null,
  firstname                 varchar(255) not null,
  lastname                  varchar(255),
  idtype                    integer,
  constraint uq_user_account_username unique (username),
  constraint pk_user_account primary key (id))
;

create table vote (
  id                        bigint not null,
  user_id                   bigint,
  criteria_id               bigint,
  project_id                bigint,
  constraint pk_vote primary key (id))
;

create table vote_criterion (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  constraint pk_vote_criterion primary key (id))
;

create sequence comment_seq;

create sequence groups_seq;

create sequence image_seq;

create sequence inbox_message_seq;

create sequence project_seq;

create sequence rate_seq;

create sequence rate_criterion_seq;

create sequence setting_seq;

create sequence user_account_seq;

create sequence vote_seq;

create sequence vote_criterion_seq;

alter table comment add constraint fk_comment_user_1 foreign key (user_id) references user_account (id) on delete restrict on update restrict;
create index ix_comment_user_1 on comment (user_id);
alter table comment add constraint fk_comment_project_2 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_comment_project_2 on comment (project_id);
alter table groups add constraint fk_groups_user_3 foreign key (user_id) references user_account (id) on delete restrict on update restrict;
create index ix_groups_user_3 on groups (user_id);
alter table groups add constraint fk_groups_project_4 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_groups_project_4 on groups (project_id);
alter table image add constraint fk_image_project_5 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_image_project_5 on image (project_id);
alter table inbox_message add constraint fk_inbox_message_sender_6 foreign key (sender_id) references user_account (id) on delete restrict on update restrict;
create index ix_inbox_message_sender_6 on inbox_message (sender_id);
alter table inbox_message add constraint fk_inbox_message_receiver_7 foreign key (receiver_id) references user_account (id) on delete restrict on update restrict;
create index ix_inbox_message_receiver_7 on inbox_message (receiver_id);
alter table inbox_message add constraint fk_inbox_message_comment_8 foreign key (comment_id) references comment (id) on delete restrict on update restrict;
create index ix_inbox_message_comment_8 on inbox_message (comment_id);
alter table rate add constraint fk_rate_user_9 foreign key (user_id) references user_account (id) on delete restrict on update restrict;
create index ix_rate_user_9 on rate (user_id);
alter table rate add constraint fk_rate_project_10 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_rate_project_10 on rate (project_id);
alter table rate add constraint fk_rate_criterion_11 foreign key (criteria_id) references rate_criterion (id) on delete restrict on update restrict;
create index ix_rate_criterion_11 on rate (criteria_id);
alter table vote add constraint fk_vote_user_12 foreign key (user_id) references user_account (id) on delete restrict on update restrict;
create index ix_vote_user_12 on vote (user_id);
alter table vote add constraint fk_vote_criterion_13 foreign key (criteria_id) references vote_criterion (id) on delete restrict on update restrict;
create index ix_vote_criterion_13 on vote (criteria_id);
alter table vote add constraint fk_vote_project_14 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_vote_project_14 on vote (project_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists comment;

drop table if exists groups;

drop table if exists image;

drop table if exists inbox_message;

drop table if exists project;

drop table if exists rate;

drop table if exists rate_criterion;

drop table if exists setting;

drop table if exists user_account;

drop table if exists vote;

drop table if exists vote_criterion;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists comment_seq;

drop sequence if exists groups_seq;

drop sequence if exists image_seq;

drop sequence if exists inbox_message_seq;

drop sequence if exists project_seq;

drop sequence if exists rate_seq;

drop sequence if exists rate_criterion_seq;

drop sequence if exists setting_seq;

drop sequence if exists user_account_seq;

drop sequence if exists vote_seq;

drop sequence if exists vote_criterion_seq;


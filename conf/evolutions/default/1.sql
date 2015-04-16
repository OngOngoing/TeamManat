# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table comment (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  project_id                bigint,
  comment                   varchar(255),
  constraint pk_comment primary key (id))
;

create table criteria (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  constraint pk_criteria primary key (id))
;

create table project (
  id                        integer auto_increment not null,
  project_name              varchar(255),
  project_description       varchar(255),
  constraint pk_project primary key (id))
;

create table project_image (
  id                        bigint auto_increment not null,
  project_id                bigint,
  image                     longblob,
  constraint pk_project_image primary key (id))
;

create table rate (
  id                        bigint auto_increment not null,
  score                     integer,
  user_id                   bigint,
  project_id                bigint,
  criteria_id               bigint,
  constraint pk_rate primary key (id))
;

create table settings (
  id                        bigint auto_increment not null,
  key_name                  varchar(255),
  key_value                 varchar(255),
  id_type                   integer,
  description               varchar(255),
  constraint uq_settings_key_name unique (key_name),
  constraint pk_settings primary key (id))
;

create table user_account (
  id                        bigint auto_increment not null,
  username                  varchar(255),
  password                  varchar(255) not null,
  firstname                 varchar(255) not null,
  lastname                  varchar(255),
  idtype                    integer,
  project_id                integer,
  constraint uq_user_account_username unique (username),
  constraint pk_user_account primary key (id))
;

create table vote (
  id                        bigint auto_increment not null,
  criterion_id              bigint,
  type                      varchar(255),
  user_id                   bigint,
  project_id                bigint,
  constraint pk_vote primary key (id))
;

create table vote_criterion (
  id                        bigint auto_increment not null,
  type                      bigint,
  name                      varchar(255),
  description               varchar(255),
  constraint pk_vote_criterion primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table comment;

drop table criteria;

drop table project;

drop table project_image;

drop table rate;

drop table settings;

drop table user_account;

drop table vote;

drop table vote_criterion;

SET FOREIGN_KEY_CHECKS=1;


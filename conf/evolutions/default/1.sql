# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table project (
  id                        integer auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  picture                   varchar(255),
  team_id                   bigint,
  constraint pk_project primary key (id))
;

create table rate (
  id                        bigint auto_increment not null,
  score                     integer,
  type                      varchar(255),
  user_id                   bigint,
  project_id                bigint,
  comment                   varchar(255),
  constraint pk_rate primary key (id))
;

create table team (
  id                        bigint auto_increment not null,
  team_name                 varchar(255),
  description               varchar(255),
  constraint pk_team primary key (id))
;

create table user_account (
  id                        bigint auto_increment not null,
  username                  varchar(255),
  password                  varchar(255),
  firstname                 varchar(255),
  lastname                  varchar(255),
  idtype                    integer,
  teamNum                   integer,
  constraint pk_user_account primary key (id))
;

create table vote (
  id                        bigint auto_increment not null,
  score                     integer,
  type                      varchar(255),
  user_id                   bigint,
  project_id                bigint,
  constraint pk_vote primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table project;

drop table rate;

drop table team;

drop table user_account;

drop table vote;

SET FOREIGN_KEY_CHECKS=1;


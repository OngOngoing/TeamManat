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

create table team (
  id                        bigint auto_increment not null,
  team_name                 varchar(255),
  description               varchar(255),
  constraint pk_team primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  username                  varchar(255),
  password                  varchar(255),
  name                      varchar(255),
  type                      integer,
  team_num                  integer,
  constraint pk_user primary key (id))
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

drop table team;

drop table user;

drop table vote;

SET FOREIGN_KEY_CHECKS=1;


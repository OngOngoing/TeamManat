# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table project (
  id                        integer primary key AUTOINCREMENT,
  name                      varchar(255),
  description               varchar(255))
;

create table user (
  id                        integer primary key AUTOINCREMENT,
  username                  varchar(255),
  password                  varchar(255),
  name                      varchar(255),
  type                      integer)
;




# --- !Downs

PRAGMA foreign_keys = OFF;

drop table project;

drop table user;

PRAGMA foreign_keys = ON;


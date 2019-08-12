drop view v_search;

create temporary table a1(auth_created_date,auth_id,auth_website,auth_name);
insert into a1 select auth_created_date,auth_id,auth_website,coalesce(auth_first_name,'') || ' ' || auth_last_name from author;
drop table author;

create table author (
   auth_created_date   datetime not null default (datetime(current_timestamp,'localtime')),
   auth_id		        integer not null primary key autoincrement,
   auth_name           varchar(200) not null,
   auth_website		  varchar(100)
);

insert into Author select auth_created_date,auth_id,auth_name,auth_website from a1;

drop table a1;

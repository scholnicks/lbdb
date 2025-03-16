-- vi: set syntax=sql ts=4 sw=4 sts=4 et ff=unix ai si :

create table Book_Type (
   bot_created_date   datetime not null default (datetime(current_timestamp,'localtime')),
   bot_modified_date  datetime,
   bot_id             integer not null primary key,
   bot_desc           varchar(80) not null
);

create table Media_Type (
   med_created_date   datetime not null default (datetime(current_timestamp,'localtime')),
   med_modified_date  datetime,
   med_id             integer not null primary key,
   med_desc           varchar(80) not null
);

create table Author (
   auth_created_date   datetime not null default (datetime(current_timestamp,'localtime')),
   auth_id             integer not null primary key autoincrement,
   auth_name           varchar(200) not null,
   auth_website        varchar(100)
);

create table Book (
   book_created_date    datetime not null default (datetime(current_timestamp,'localtime')),
   book_modified_date   datetime,
   book_id              integer not null primary key autoincrement,
   bot_id               integer not null,
   med_id               integer not null,
   book_title           varchar(100) not null,
   book_anthology       char(1) not null default 'n',
   book_series          varchar(100),
   book_published_year  varchar(4),
   book_isbn            varchar(20),
   book_number_of_pages integer,
   book_comments        varchar(255),
   foreign key(bot_id) references Book_Type(bot_id),
   foreign key(med_id) references Media_Type(med_id)
);

create table Author_Book_Xref (
   abx_created_date     datetime not null default (datetime(current_timestamp,'localtime')),
   abx_modified_date    datetime,
   auth_id              integer not null,
   book_id              integer not null,
   abx_editor           char(1) not null default 'n',
   primary key (auth_id,book_id),
   foreign key(auth_id) references Author(auth_id),
   foreign key(book_id) references Book(book_id)
);

insert into Book_Type(bot_id,bot_desc) values(1,'Fiction');
insert into Book_Type(bot_id,bot_desc) values(2,'Non-Fiction');
insert into Book_Type(bot_id,bot_desc) values(3,'Technical');

insert into Media_Type(med_id,med_desc) values(1,'Book');
insert into Media_Type(med_id,med_desc) values(2,'Kindle');
insert into Media_Type(med_id,med_desc) values(3,'Nook');
insert into Media_Type(med_id,med_desc) values(4,'Audible Audio Book');



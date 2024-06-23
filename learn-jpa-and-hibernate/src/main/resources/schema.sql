create table courses (
	id bigint unique not null,
	name varchar(255) not null,
	author varchar(255) not null,
	primary key (id)
);
create database hstestdb;

use hstestdb;

create table HTML(
	uuid	char(36)	primary key,
	content	text		not null
);

CREATE USER hsdb@localhost IDENTIFIED BY 'hsdbpass';

grant all on hstestdb.* to hsdb@localhost identified by 'hsdbpass';

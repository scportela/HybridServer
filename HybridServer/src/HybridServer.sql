create database hstestdb;

use hstestdb;

create table HTML(
	uuid	char(36)	primary key,
	content	text		not null
);

create table XML(
	uuid	char(36)	primary key,
	content	text		not null
);

create table XSD(
	uuid	char(36)	primary key,
	content	text		not null
);

create table XSLT(
	uuid	char(36)	primary key,
	content	text		not null,
	xsd	char(36)	not null
);

CREATE USER hsdb@localhost IDENTIFIED BY 'hsdbpass';

grant all on hstestdb.* to hsdb@localhost identified by 'hsdbpass';

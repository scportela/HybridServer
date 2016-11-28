create database hstestdb;

use hstestdb;

create table HTML(
	uuid	char(36)	primary key,
	content	text
);

create table XML(
	uuid	char(36)	primary key,
	content	text
);

create table XSD(
	uuid	char(36)	primary key,
	content	text
);

create table XSLT(
	uuid	char(36)	primary key,
	content	text,
	xsd	char(36)
);

CREATE USER hsdb@localhost IDENTIFIED BY 'hsdbpass';

grant all on hstestdb.* to hsdb@localhost identified by 'hsdbpass';

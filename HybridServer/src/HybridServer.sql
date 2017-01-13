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

create database hstestdb1;

use hstestdb1;

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

create database hstestdb2;

use hstestdb2;

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

create database hstestdb3;

use hstestdb3;

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

create database hstestdb4;

use hstestdb4;

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
grant all on hstestdb1.* to hsdb@localhost identified by 'hsdbpass';
grant all on hstestdb2.* to hsdb@localhost identified by 'hsdbpass';
grant all on hstestdb3.* to hsdb@localhost identified by 'hsdbpass';
grant all on hstestdb4.* to hsdb@localhost identified by 'hsdbpass';

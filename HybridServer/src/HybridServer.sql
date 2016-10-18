create database hstestdb;

use hstestdb;

create table page(
	uuid	char(36)	primary key,
	content	text		not null
);

CREATE USER IF NOT EXISTS 'hsdb'@'localhost' IDENTIFIED BY 'hsdbpass';

GRANT ALL ON hstestbd TO 'hsdb'@'localhost';

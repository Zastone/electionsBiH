-- SQL Script that generates structure for Elections project

-- drop database if exists `elections`;
-- create database elections;

use elections;

drop table if exists `municipalities`;

create table municipalities (
	municipality_id int,
	municipality_name char(50),
	kanton int NULL,
	parliament_bih int NULL,
	parliament_fbih int NULL,
	parliament_rs int NULL,
	president_bih int NULL,
	president_rs_election int NULL,
	PRIMARY KEY (municipality_id)
	);

drop table if exists `muni_temp`;

create table muni_temp (
	municipality_id int,
	municipality_name char(50),
	kanton char(4) NULL,
	parliament_bih char(4) NULL,
	parliament_fbih char(4) NULL,
	parliament_rs char(4) NULL,
	president_bih char(4) NULL,
	president_rs_election char(4) NULL,
	PRIMARY KEY (municipality_id)
	);


drop table if exists `muni`;

create table muni (
	municipality_id int,
	municipality_name char(50),
	PRIMARY KEY (municipality_id)
	);



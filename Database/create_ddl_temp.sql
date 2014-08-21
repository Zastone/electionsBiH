-- SQL Script that generates structure for Elections project

-- drop database if exists `elections`;
create database elections;

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

drop table if exists `parliament_seats`;

create table `parliament_seats` (
	election_unit_id int,
	count_seats int,
	race_name char(50),
	PRIMARY KEY (election_unit_id)
	);

-- select distinct race_name from parliament_seats;

select distinct (race_name), length(race_name) from parliament_seats;

-- aggregate results table

drop table if exists `results`;
create table `results` (
	election_unit_id int,
	race_name char(50),
	year int,
	polling_station_id char(12),
	municipality_id int,
	party char(200),
	vote_count int,
	candidate_name char(200) null
	)




create database elections;

use elections;

create table municipalities (
	municipality_id int,
	municipality_name char(50),
	kanton int,
	parliament_bih int,
	parliament_fbih int,
	parliament_rs int,
	president_bih int,
	president_rs_election int,
	PRIMARY KEY (municipality_id)
	);

create table muni (
	municipality_id int,
	municipality_name char(50),
	PRIMARY KEY (municipality_id)
	);



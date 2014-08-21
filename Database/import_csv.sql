use elections;

-- quick test
truncate table muni;
load data infile '/data_in/muni2.csv' INTO TABLE muni
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

-- municipalities - handle nulls in CSVs 
truncate table municipalities;
load data infile '/data_in/municipality-ids-election-units-pivot.csv' INTO TABLE muni_temp
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

SET SQL_SAFE_UPDATES=0;
update muni_temp set kanton = NULL where kanton = '';
update muni_temp set parliament_bih = NULL where parliament_bih = '';
update muni_temp set parliament_fbih = NULL where parliament_fbih = '';
update muni_temp set parliament_rs = NULL where parliament_rs = '';
update muni_temp set president_bih = NULL where president_bih = '';
update muni_temp set president_rs_election = NULL where president_rs_election = '';

truncate table municipalities;
insert into municipalities
select  *
from muni_temp mt;

truncate table parliament_seats;
load data infile '/data_in/parliament-seats-by-election-unit.csv' INTO TABLE parliament_seats
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

-- TBD

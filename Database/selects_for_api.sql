-- select statements for elections API

use elections;

-- municipalities

select municipality_id,
	municipality_name,
	kanton,
	parliament_bih,
	parliament_fbih,
	parliament_rs,
	president_bih,
	president_rs_election
from municipalities;

-- results selection base start
select party, sum(vc)
from (
select r.party, r.municipality_id, sum(r.vote_count) vc
from results r
where r.year = 2010 and municipality_id = 7
group by r.party, r.municipality_id) sum_base 
group by party

-- results selection

SELECT
  r.party, r.party_abbrev, r.municipality_id, m2.municipality_name, r.year,
  m.election_type, m.election_unit_id, sum(r.vote_count) AS count_votes
  FROM results r
  INNER JOIN muni_for_results m ON r.municipality_id = m.municipality_id AND m.election_type = r.race_name
  INNER JOIN municipalities m2 ON r.municipality_id = m2.municipality_id
  INNER JOIN parliament_seats p ON m.election_unit_id = p.election_unit_id
  -- WHERE r.year = $year
  WHERE r.year = 2010
  -- AND r.race_name = ${electionType.toString}
  AND r.race_name = 'parliament_rs'
  GROUP BY r.party, r.party_abbrev, r.municipality_id, m2.municipality_name, r.year, m.election_type,  m.election_unit_id
  ORDER BY r.party, r.municipality_id;

-- selections for math:  
-- 1) number of compensatory seats 
select election_unit_id, count_seats, race_name from parliament_seats 
where election_unit_id in (300,400,501,502);

-- 2) number of votes by electoral unit and party
SELECT
  r.party, r.party_abbrev, r.year,
  m.election_type, m.election_unit_id, sum(r.vote_count) AS count_votes
  FROM results r
  INNER JOIN muni_for_results m ON r.municipality_id = m.municipality_id AND m.election_type = r.race_name
  INNER JOIN municipalities m2 ON r.municipality_id = m2.municipality_id
  INNER JOIN parliament_seats p ON m.election_unit_id = p.election_unit_id
  -- WHERE r.year = $year
  WHERE r.year = 2010
  -- AND r.race_name = ${electionType.toString}
  AND r.race_name = 'parliament_rs'
  GROUP BY r.party, r.party_abbrev, r.year, m.election_type,  m.election_unit_id
  ORDER BY r.party, r.municipality_id;


select * from municipalities

select * from muni_temp

select party, sum(vote_count), count(*) 
from results 
where year = 2010 and race_name = 'parliament_rs'
group by party

select year, count(distinct party), sum(vote_count), count(*) from results group by year

select race_name, count(*) from results where year = 2010 and municipality_id = 6 and party = '' group by race_name
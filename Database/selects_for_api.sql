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
select
r.party, r.party_abbrev, r.municipality_id, m2.municipality_name, r.year,  m.election_type, m.election_unit_id, sum(r.vote_count) count_votes
from results r 
inner join muni_for_results m on r.municipality_id = m.municipality_id
inner join municipalities m2 on r.municipality_id = m2.municipality_id
inner join parliament_seats p on m.election_unit_id = p.election_unit_id
where r.year = 2010 
and m.election_type = 'parliament_rs'
group by r.party, r.party_abbrev, r.municipality_id, m2.municipality_name, r.year, m.election_type,  m.election_unit_id
order by r.party, r.municipality_id

select * from municipalities

select * from muni_temp
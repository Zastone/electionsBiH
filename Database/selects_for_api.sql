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


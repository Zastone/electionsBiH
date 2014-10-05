package ba.zastone.elections.repos

import ba.zastone.elections.db.SQLDatabase
import ba.zastone.elections.model._

import scala.slick.jdbc.GetResult
import scala.slick.jdbc.StaticQuery.interpolation

case class ElectoralResultsTuple(party: String, abbreviation: String,
                                 year: Int, electionUnitId: ElectionUnitId, voteCounts: Int) {

  def toPartyResults = PartyResult(party, abbreviation, voteCounts)

}

class MandatesDao(protected val database: SQLDatabase, parliamentSeatsDao: ParliamentSeatsDao) {

  import database._

  implicit val electoralResultsTupleTransformer = GetResult(r =>
    ElectoralResultsTuple(r.<<, r.<<, r.<<, ElectionUnitId(r.<<), r.<<)
  )

  def partyResultsPerElectoralUnit(request: Election): List[ElectoralResultsTuple] = {
    val query = sql"""
    SELECT r.party, r.party_abbrev, r.year, m.election_unit_id, sum(r.vote_count) AS count_votes
    FROM results r
    INNER JOIN muni_for_results m ON r.municipality_id = m.municipality_id AND m.election_type = r.race_name
    INNER JOIN municipalities m2 ON r.municipality_id = m2.municipality_id
    INNER JOIN parliament_seats p ON m.election_unit_id = p.election_unit_id
    WHERE r.year = ${request.year}
    AND r.race_name = ${request.electionType.toString}
    GROUP BY r.party, r.party_abbrev, r.year, m.election_type,  m.election_unit_id
    ORDER BY r.party, r.municipality_id
  """.as[ElectoralResultsTuple]

    db.withSession { implicit session =>{}
      query.list
    }
  }
}

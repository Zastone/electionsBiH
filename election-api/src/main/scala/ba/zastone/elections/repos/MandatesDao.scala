package ba.zastone.elections.repos

import ba.zastone.elections.db.SQLDatabase
import ba.zastone.elections.model.ElectionTypes._
import ba.zastone.elections.model.{Election, ElectionTypes, PartyResult}
import ba.zastone.elections.repos.BiHEntities.BiHEntity

import scala.slick.jdbc.GetResult
import scala.slick.jdbc.StaticQuery.interpolation

// TODO remember about the electoral unit name

object BiHEntities extends Enumeration {
  val FBiH = Value("fbih")
  val RS = Value("rs")

  type BiHEntity = Value
}

case class ElectionUnitId(value: Int) extends AnyVal

case class ElectoralUnit(electionType: ElectionType, electionUnitId: ElectionUnitId, seats: Int,
                              auxiliaryEntity: Option[BiHEntity]) {

  val isCompensatory = electionUnitId.value % 100 == 0

  val compensatoryElectionUnitId = ElectionUnitId(electionUnitId.value / 100 * 100) /* integer math */

}

case class ElectoralResultsTuple(party: String, abbreviation: String,
                                 year: Int, electionUnitId: ElectionUnitId, voteCounts: Int) {

  def toPartyResults = PartyResult(party, abbreviation, voteCounts)

}

class MandatesDao(protected val database: SQLDatabase) {

  import database._

  implicit val electoralUnitSeatsTransformer = GetResult(r =>
    ElectoralUnit(ElectionTypes.withName(r.nextString()), ElectionUnitId(r.nextInt()), r.nextInt(), None)
  )

  implicit val electoralResultsTupleTransformer = GetResult(r =>
    ElectoralResultsTuple(r.<<, r.<<, r.<<, ElectionUnitId(r.<<), r.<<)
  )

  def seatCounts(request: Election): List[ElectoralUnit] = {
    val query = sql"SELECT race_name, election_unit_id, count_seats FROM parliament_seats".as[ElectoralUnit]

    db.withSession { implicit session =>
      query.list.filter(_.electionType == request.electionType)
    }
  }

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

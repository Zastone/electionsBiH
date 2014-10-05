package ba.zastone.elections.repos

import ba.zastone.elections.db.SQLDatabase
import ba.zastone.elections.model.{ElectionUnitId, ElectionTypes, ElectoralUnit, Election}

import scala.slick.jdbc.GetResult
import scala.slick.jdbc.StaticQuery.interpolation

class ParliamentSeatsDao(protected val database: SQLDatabase) {

  import database._

  implicit val electoralUnitSeatsTransformer = GetResult(r =>
    ElectoralUnit(ElectionTypes.withName(r.nextString()), ElectionUnitId(r.nextInt()), r.nextInt(),
      r.nextIntOption() map ElectionUnitId, r.nextBoolean())
  )

  def forElection(request: Election): List[ElectoralUnit] = {
    val query =
      sql"""SELECT race_name, election_unit_id, count_seats, compensatory_id, compensatory
              FROM parliament_seats""".as[ElectoralUnit]

    db.withSession { implicit session =>
      query.list.filter(_.electionType == request.electionType)
    }
  }

  /**
   * @return map of compensatory [[ElectoralUnit]]s keyed by their [[ElectionUnitId]].
   */
  def seatCountsByElectionUnitId(election: Election) : Map[ElectionUnitId, ElectoralUnit] =
    forElection(election).map(e => (e.electionUnitId, e)).toMap

}

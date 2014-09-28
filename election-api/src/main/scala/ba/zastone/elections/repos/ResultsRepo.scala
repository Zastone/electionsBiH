package ba.zastone.elections.repos

import ba.zastone.elections.db.SQLDatabase
import ba.zastone.elections.metrics.Metrics
import ba.zastone.elections.model.{Election, MunicipalityResult, ResultsResponse}

import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.jdbc.{GetResult, StaticQuery => Q}

case class ResultsTuple(party: String, abbrev: String, municipalityId: Int, municipalityName: String,
                        year: Int, electionType: String, electionUnitId: Int, votes: Int)

class ResultsRepo(protected val database: SQLDatabase) extends Metrics {

  private val queryTimer = metrics.timer("query")
  private val transformationTimer = metrics.timer("transformation")

  import database._

  // http://slick.typesafe.com/doc/2.1.0/sql.html#query-statements
  implicit val getsResultsTuple =
    GetResult(r => ResultsTuple(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))

  def findAll(election: Election): List[ResultsTuple] = {
    queryTimer.time {
      val query =
        sql"""
  SELECT
  r.party, r.party_abbrev, r.municipality_id, m2.municipality_name, r.year,
  m.election_type, m.election_unit_id, sum(r.vote_count) AS count_votes
  FROM results r
  INNER JOIN muni_for_results m ON r.municipality_id = m.municipality_id AND m.election_type = r.race_name
  INNER JOIN municipalities m2 ON r.municipality_id = m2.municipality_id
  INNER JOIN parliament_seats p ON m.election_unit_id = p.election_unit_id
  WHERE r.year = ${election.year}
  AND r.race_name = ${election.electionType.toString}
  GROUP BY r.party, r.party_abbrev, r.municipality_id, m2.municipality_name, r.year, m.election_type,  m.election_unit_id
  ORDER BY r.party, r.municipality_id;
    """.as[ResultsTuple]

      db.withSession { implicit session =>
        query.list
      }
    }
  }

  def results(election: Election) : ResultsResponse = {
    val ed = findAll(election)
    ed match {
      case Nil => throw new ElectionDataNotFound(election)
      case allResults: List[ResultsTuple] =>
        val groupedByMunicipalityId: Map[Int, List[ResultsTuple]] =
          allResults.groupBy((tuple) => tuple.municipalityId)

        transformationTimer.time {
          val unorderedResults = groupedByMunicipalityId.foldLeft(ResultsResponse.withoutResults(election)) {
            case (response, (municipalityId, resultsList)) =>
              val municipalityName = resultsList.head.municipalityName
              val municipalityResults = MunicipalityResult.withoutResults(municipalityId, municipalityName)

              response.withMunicipalityResult(resultsList.foldLeft(municipalityResults) {
                case (acc, tuple) =>
                  acc.add(tuple)
              })
          }
          unorderedResults.copy(municipalityResults = unorderedResults.municipalityResults.sortBy(_.id))
        }
    }
  }

}
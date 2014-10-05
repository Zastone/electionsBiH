package ba.zastone.elections.repos

import ba.zastone.elections.infrastructure.Beans
import ba.zastone.elections.model.{ElectoralUnit, ElectionUnitId, ElectionTypes, Election}
import org.scalatest._

class ParliamentSeatsDaoSpec extends FlatSpec with ShouldMatchers with SQLSupport {

  val beans = Beans

  val dao = beans.parliamentSeatsDao

  behavior of classOf[ParliamentSeatsDao].getSimpleName

  case class ExpectedElectionUnit(election: Election, electionUnit: ElectoralUnit)

  val expectedElectionUnitData = List(
    ExpectedElectionUnit(Election(ElectionTypes.ParlimentBih, 2010),
      ElectoralUnit(ElectionTypes.ParlimentBih, ElectionUnitId(510), 5, None, compensatory = true)),
    ExpectedElectionUnit(Election(ElectionTypes.ParlimentBih, 2010),
      ElectoralUnit(ElectionTypes.ParlimentBih, ElectionUnitId(511), 3, Some(ElectionUnitId(510)), compensatory = false)),
    ExpectedElectionUnit(Election(ElectionTypes.Kanton, 2008),
      ElectoralUnit(ElectionTypes.Kanton, ElectionUnitId(201), 30, None, compensatory = false)
    )
  )

  for (expected <- expectedElectionUnitData) {
    val electionUnitId = expected.electionUnit.electionUnitId
    it should s"correctly deserialize ${electionUnitId.value} compensatory election unit" in {
      dao.forElection(expected.election).find(_.electionUnitId == electionUnitId) match {
        case None => fail(s"Data for Election Unit ${electionUnitId.value} not found")
        case Some(actual) =>
          expected.electionUnit shouldEqual actual
      }
    }
  }

  it should "deserialize all ParliamentBih election unit data" in {
    val electionUnitData = dao.forElection(Election(ElectionTypes.ParlimentBih, 2010))

    electionUnitData.size shouldEqual 10
  }

}

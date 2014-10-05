package ba.zastone.elections.repos

import ba.zastone.elections.mandates.{PartyKey, PartyElectionResult, PartiesMandatesComputer}
import ba.zastone.elections.metrics.Metrics
import ba.zastone.elections.model._

class MandatesService(mandatesDao: MandatesDao, parliamentSeatsDao: ParliamentSeatsDao) extends Metrics {

  private val transformationTimer = metrics.timer("transformation")

  private def compensatorySeats(election: Election) =
    parliamentSeatsDao.seatCountsByElectionUnitId(election).filter { case (_, v) => v.isCompensatory}

  /**
   * Computes and returns party mandates for a given [[Election]].
   */
  def mandates(election: Election) =
    MandatesResponse.fromRequest(election).withElectedParties(electedParties(election))

  /**
   * Computes the list of elected parties (party mandates).
   */
  def electedParties(election: Election): List[ElectedParty] = {
    mandatesDao.partyResultsPerElectoralUnit(election) match {
      case results : List[ElectoralResultsTuple] if results.nonEmpty =>
        computeElectedPartiesFromTuples(election, results)
      case Nil => throw new ElectionDataNotFound(election)
    }
  }

  private def computeElectedPartiesFromTuples(election: Election, results: List[ElectoralResultsTuple]) = {
    val electoralUnitSeatsMap = parliamentSeatsDao.seatCountsByElectionUnitId(election)

    transformationTimer.time {
      val resultsByElectionUnitId = results.groupBy(_.electionUnitId).toList

      new MandatesComputer(resultsByElectionUnitId.map {
        case (electoralUnitId, electoralResultsTuples) =>
          (electoralUnitSeatsMap(electoralUnitId), electoralResultsTuples.map(_.toPartyResults))
      }, compensatorySeats(election)).computeMandates()
    }
  }
}

case class ElectedParty(partyResult: PartyResult, electoralUnitId: ElectionUnitId,
                        electoralUnitName: Option[String])

object ElectedParty {

  def from(electoralUnitSeats: ElectoralUnit, partyResult: PartyResult) =
    ElectedParty(partyResult, electoralUnitSeats.electionUnitId, None)

}

/**
 *
 * @param electoralData [[PartyResult]] by [[ElectoralUnit]]
 * @param compensatoryElectoralUnits compensatory electoral units
 */
class MandatesComputer(electoralData: List[(ElectoralUnit, List[PartyResult])],
                       compensatoryElectoralUnits: Map[ElectionUnitId, ElectoralUnit]) {

  /* There is no need to inject this dependency as [[MandatesComputer]] will never be tested without it. */
  val partiesMandatesComputer = new PartiesMandatesComputer()

  /**
   * Map of party name to party name abbreviation.
   */
  lazy val partyAbbreviation: Map[String, String] = {
    for {
      (_, partyResults) <- electoralData
      partyResult <- partyResults
    } yield (partyResult.name, partyResult.abbreviation)
  }.toMap

  lazy val partyDataLookup: Map[PartyKey, (ElectoralUnit, PartyResult)] = electoralData.flatMap {
    case (electionUnit, partyResultsList) =>
      partyResultsList.map { partyResults =>
        (PartyKey(electionUnit.electionUnitId, partyResults.name),
          (electionUnit, partyResults))
      }
  }.toMap

  private def buildElectedPartyFrom(electionResult: PartyElectionResult) = {
    val partyName = electionResult.key.partyName
    val electionUnitId = electionResult.key.electionUnitId
    ElectedParty(
      PartyResult(partyName, partyAbbreviation(partyName), electionResult.votes),
      electionUnitId,
      electoralUnitName = None
    )
  }

  private def availableSeatsInCompensatoryUnit(id: ElectionUnitId): Int = compensatoryElectoralUnits.get(id) match {
    case Some(electionUnit) => electionUnit.seats
    case None => 0
  }

  private def compensatoryElectionUnitOf(partyId: PartyKey) = partyDataLookup(partyId)._1.compensatoryElectionUnitId

  private def partyElectionResultsListForCompensatorySeats(notElectedDirectly: List[PartyElectionResult])
  : Map[ElectionUnitId, List[PartyElectionResult]] = {
    val withCompensatorySeatIds = notElectedDirectly.map {
      r => r.electionUnitIdUpdated(compensatoryElectionUnitOf(r.key))
    }

    val withSummedResults = withCompensatorySeatIds.foldLeft(Map[PartyKey, PartyElectionResult]()) {
      case (acc, partyResult) =>
        acc.updated(partyResult.key,
          acc.get(partyResult.key) match {
            case None => partyResult
            case Some(thatPartyResult) => thatPartyResult + partyResult
          }
        )
    }.values.toList

    withSummedResults.groupBy(_.key.electionUnitId)

  }

  def computeMandates() = {
    val mandateAssignments = electoralData.map {
      case (electionUnit, partyResultsList) =>
        val partyElectionResultsList = partyResultsList.map {
          r => PartyElectionResult(PartyKey(electionUnit.electionUnitId, r.name), r.votes)
        }
        partiesMandatesComputer.computeMandates(partyElectionResultsList, electionUnit.seats)
    }

    val partiesElectedDirectly = mandateAssignments.flatMap(_.elected).map(buildElectedPartyFrom)
    val candidatesForCompensatorySeats = mandateAssignments.flatMap(_.notElected)

    val forCompensatorySeats = partyElectionResultsListForCompensatorySeats(candidatesForCompensatorySeats)

    val partiesElectedToCompensatorySeats = forCompensatorySeats.map {
      case (electionUnitId, partyElectionResults) =>
        partiesMandatesComputer.computeMandates(partyElectionResults,
          availableSeatsInCompensatoryUnit(electionUnitId))
    }.flatMap(_.elected).map(buildElectedPartyFrom)

    partiesElectedDirectly ++ partiesElectedToCompensatorySeats
  }

}
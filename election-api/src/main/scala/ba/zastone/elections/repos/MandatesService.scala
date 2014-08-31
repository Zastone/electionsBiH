package ba.zastone.elections.repos

import ba.zastone.elections.mandates.{PartyId, PartyElectionResult, PartiesMandatesComputer}
import ba.zastone.elections.model._

class MandatesService(dao: MandatesDao) {

  private def compensatorySeats(election: Election) =
    dao.seatCountsByElectionUnitId(election).filter { case (_, v) => v.isCompensatory}

  def mandates(election: Election) =
    MandatesResponse.fromRequest(election).withElectedParties(electedParties(election))

  def electedParties(election: Election): List[ElectedParty] = {
    dao.partyResultsPerElectoralUnit(election) match {
      case Nil =>
        throw new ElectionDataNotFound(election)
      case results =>
        val electoralUnitSeatsMap = dao.seatCountsByElectionUnitId(election)

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

class MandatesComputer(electoralData: List[(ElectoralUnit, List[PartyResult])],
                       compensatoryElectoralUnits: Map[ElectionUnitId, ElectoralUnit]) {

  /* There is no need to inject this dependency as [[MandatesComputer]] will never be tested without it. */
  val partiesMandatesComputer = new PartiesMandatesComputer()

  lazy val partyAbbreviation: Map[String, String] = {
    for {
      (_, partyResults) <- electoralData
      partyResult <- partyResults
    } yield (partyResult.name, partyResult.abbreviation)
  }.toMap

  lazy val partyDataLookup: Map[PartyId, (ElectoralUnit, PartyResult)] = electoralData.flatMap {
    case (electionUnit, partyResultsList) =>
      partyResultsList.map { partyResults =>
        (PartyId(electionUnit.electionUnitId, partyResults.name),
          (electionUnit, partyResults))
      }
  }.toMap

  private def buildElectedPartyFrom(electionResult: PartyElectionResult) = {
    val partyName = electionResult.id.partyName
    ElectedParty(
      PartyResult(partyName, partyAbbreviation(partyName), electionResult.votes),
      electionResult.id.electionUnitId,
      None
    )
  }

  private def availableSeatsInCompensatoryUnit(id: ElectionUnitId): Int = compensatoryElectoralUnits(id).seats

  private def compensatoryElectionUnitOf(partyId: PartyId) = partyDataLookup(partyId)._1.compensatoryElectionUnitId

  private def partyElectionResultsListForCompensatorySeats(notElectedDirectly: List[PartyElectionResult])
  : Map[ElectionUnitId, List[PartyElectionResult]] = {
    val withCompensatorySeatIds = notElectedDirectly.map {
      r => r.electionUnitIdUpdated(compensatoryElectionUnitOf(r.id))
    }

    val withSummedResults = withCompensatorySeatIds.foldLeft(Map[PartyId, PartyElectionResult]()) {
      case (acc, partyResult) =>
        acc.updated(partyResult.id,
          acc.get(partyResult.id) match {
            case None => partyResult
            case Some(thatPartyResult) => thatPartyResult + partyResult
          }
        )
    }.values.toList

    withSummedResults.groupBy(_.id.electionUnitId)

  }

  def computeMandates() = {
    val mandateAssignments = electoralData.map {
      case (electionUnit, partyResultsList) =>
        val partyElectionResultsList = partyResultsList.map {
          r => PartyElectionResult(PartyId(electionUnit.electionUnitId, r.name), r.votes)
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
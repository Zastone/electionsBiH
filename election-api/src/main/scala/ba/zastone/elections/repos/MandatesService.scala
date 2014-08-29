package ba.zastone.elections.repos

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

case class VoteFraction(fraction: Float, partyResults: PartyResult)

object VoteFraction {
  def ofOrder(order: Int, partyResults: PartyResult) = VoteFraction(partyResults.votes / (order * 2 + 1), partyResults)
}

case class ElectedParty(partyResult: PartyResult, electoralUnitId: ElectionUnitId,
                        electoralUnitName: Option[String], seatIndex: Int)

object ElectedParty {

  def from(electoralUnitSeats: ElectoralUnit, partyResult: PartyResult, index: Int) =
    ElectedParty(partyResult, electoralUnitSeats.electionUnitId, None, index)

}

class MandatesComputer(electoralData: List[(ElectoralUnit, List[PartyResult])],
                       compensatoryElectoralUnits: Map[ElectionUnitId, ElectoralUnit]) {

  private def computeVoteFractions(partyResults: List[PartyResult], availableSeats: Int): IndexedSeq[VoteFraction] = {
    (0 to availableSeats).flatMap { order =>
      partyResults.map(VoteFraction(order, _))
    }.sortBy(-_.fraction)
  }

  private def computeElectedParties(partyResults: List[PartyResult], seatData: ElectoralUnit)
  : IndexedSeq[ElectedParty] = {
    val sortedVoteFractions = computeVoteFractions(partyResults, seatData.seats)
    // TODO handle the case of a tie
    // TODO handle election treshold
    // add a test for it
    val elected = sortedVoteFractions.take(seatData.seats).zipWithIndex
    elected.map { case (voteFraction, index) =>
      ElectedParty.from(seatData, voteFraction.partyResults, index)
    }
  }

  private def candidatePartiesForCompensatorySeats(electedParties: IndexedSeq[ElectedParty],
                                                   partyResults: List[PartyResult],
                                                   directElectionElectoralUnit: ElectoralUnit)
  : (ElectoralUnit, List[PartyResult]) = {
    val electedPartiesResultSet = electedParties.map(_.partyResult).toSet

    (directElectionElectoralUnit, partyResults.filterNot(electedPartiesResultSet(_)))
  }

  private def assignCompensatoryElectoralUnits(candidatesForCompensatorySeats: List[(ElectoralUnit, List[PartyResult])],
                                               compensatorySeatsById: Map[ElectionUnitId, ElectoralUnit])
  : List[(ElectoralUnit, List[PartyResult])] = {


    candidatesForCompensatorySeats.foldLeft(Map[ElectoralUnit, List[PartyResult]]()) {
      case (acc, (directElectionUnit, partyResults)) =>
        //        (partyResults, compensatorySeatsById(directElectionUnit.compensatoryElectionUnitId))
        val compensatoryElectoralUnit = compensatorySeatsById(directElectionUnit.compensatoryElectionUnitId)

        acc.get(compensatoryElectoralUnit) match {
          case None => acc.updated(compensatoryElectoralUnit, partyResults)
          case Some(otherPartyResults) => acc.updated(compensatoryElectoralUnit, partyResults ++ otherPartyResults)
        }
    }.toList

  }

  def computeMandates() = {
    val directElectionData = electoralData.map {
      case (electionUnit, partyResultsList) =>
        val electedParties = computeElectedParties(partyResultsList, electionUnit)
        val candidatesForCompensatorySeats = candidatePartiesForCompensatorySeats(
          electedParties, partyResultsList, electionUnit)
        (electedParties, candidatesForCompensatorySeats)
    }
    val partiesElectedDirectly = directElectionData.flatMap(_._1)
    val candidatesForCompensatorySeats = directElectionData.map(_._2)

    val partiesForCompensatorySeats =
      assignCompensatoryElectoralUnits(candidatesForCompensatorySeats, compensatoryElectoralUnits)

    val partiesElectedToCompensatorySeats = partiesForCompensatorySeats.flatMap(d => computeElectedParties _ tupled d.swap)

    partiesElectedDirectly ++ partiesElectedToCompensatorySeats
  }

}
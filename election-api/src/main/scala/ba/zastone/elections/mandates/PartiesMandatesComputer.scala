package ba.zastone.elections.mandates

import ba.zastone.elections.model.ElectionUnitId

case class ElectionThreshold(percentage: Int) extends AnyVal

case class PartyId(electionUnitId: ElectionUnitId, partyName: String)

case class PartyElectionResult(id: PartyId, votes: Int) {

  def +(that: PartyElectionResult) = {
    require(this.id == that.id)
    copy(votes = this.votes + that.votes)
  }

  def electionUnitIdUpdated(electionUnitId: ElectionUnitId) = copy(id = id.copy(electionUnitId = electionUnitId))

}

/**
 * Results of assigning seats to a list of parties represented by [[PartyElectionResult]].
 *
 * @param elected parties elected.
 * @param notElected parties not elected. Might be used for compensatory seat assignments.
 * @param lastPartyTiedWith we take a naive approach where we assume that only the two last parties might be tied.
 *                          One of them becomes the last element of the `elected` list, the other one is stored here.
 */
case class PartyMandateAssignment(elected: Seq[PartyElectionResult],
                                  notElected: Seq[PartyElectionResult],
                                  lastPartyTiedWith: Option[PartyElectionResult] = None)

case class VoteFraction(fraction: Double, result: PartyElectionResult)

object BiHVoteFractionGenerator {
  def transformResultsToOrderedFractionList(fractionsToGenerate: Int, result: Seq[PartyElectionResult]) = {
    fractionDivisors(fractionsToGenerate).flatMap { div =>
      result.map {
        result => VoteFraction(result.votes / div, result)
      }
    }.sortBy(-_.fraction)
  }

  def fractionDivisors(fractionsToGenerate: Int) = (0 until fractionsToGenerate).map(_ * 2 + 1)

}

class PartiesMandatesComputer(electionThreshold: ElectionThreshold = ElectionThreshold(3)) {

  def computeMandates(electionResult: Seq[PartyElectionResult], availableSeats: Int): PartyMandateAssignment = {
    val (eligibleForObtainingMandates, belowThreshold) = partitionByOnElectionThreshold(electionResult)
    val fractions = BiHVoteFractionGenerator.
      transformResultsToOrderedFractionList(availableSeats, eligibleForObtainingMandates)

    determineElectedObjects(fractions, availableSeats, electionResult)
  }

  private def determineElectedObjects(fractions: IndexedSeq[VoteFraction], availableSeats: Int,
                                      electionResult: Seq[PartyElectionResult])
  : PartyMandateAssignment = {
    availableSeats match {
      case 0 => PartyMandateAssignment(Nil, electionResult, None)
      case s if s > 0 =>
        val electedByFraction = fractions.take(availableSeats)
        val lastPartyTiedWith =
          if (fractions(availableSeats).fraction == electedByFraction.last.fraction)
            Some(fractions(availableSeats).result)
          else
            None

        val elected = electedByFraction.map(_.result)

        val electedSet = elected.toSet
        val electedSetWithTiedParty = lastPartyTiedWith.fold(electedSet)(electedSet + _)

        val notElected = electionResult.filterNot(electedSetWithTiedParty)

        PartyMandateAssignment(elected,
          notElected,
          lastPartyTiedWith)
      case _ =>
        throw new IllegalArgumentException("Illegal number of available seats. Cannot be less than zero. " +
          s"Was: $availableSeats")
    }

  }

  private def partitionByOnElectionThreshold(electionResult: Seq[PartyElectionResult])
  : (Seq[PartyElectionResult], Seq[PartyElectionResult]) = {
    val totalVoteCount = electionResult.map(_.votes).sum
    def isAboveThreshold(r: PartyElectionResult) = totalVoteCount * electionThreshold.percentage < r.votes * 100
    electionResult.partition(isAboveThreshold)
  }

}

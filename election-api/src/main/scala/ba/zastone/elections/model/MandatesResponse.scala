package ba.zastone.elections.model

import ba.zastone.elections.repos.ElectedParty


case class MandatesResponse(request: Election, electoralUnitMandates: List[ElectoralUnitMandates]) {

  def withElectedParties(electedParties: List[ElectedParty]) = {
    copy(electoralUnitMandates =
      electedParties.groupBy(party => (party.electoralUnitId, party.electoralUnitName)).map {
        case ((electionUnitId, electionUnitName), electedPartiesForElectionUnit) =>
          ElectoralUnitMandates.fromResults(electionUnitId, electionUnitName,
            electedPartiesForElectionUnit)
      }.toList.sortBy(_.electoralUnitId)
    )
  }
}

object MandatesResponse {
  def fromRequest(request: Election) = MandatesResponse(request, Nil)
}

case class ElectoralUnitMandates(electoralUnitName: Option[String], electoralUnitId: ElectionUnitId, mandates: List[Mandate]) {
  def mandatesSum() = mandates.map(_.seats).sum
}

object ElectoralUnitMandates {

  def fromResults(electoralUnitId: ElectionUnitId, electoralUnitName: Option[String],
                  electedParties: List[ElectedParty]) = {
    ElectoralUnitMandates(electoralUnitName, electoralUnitId,
      electedParties.groupBy(party => party.partyResult.name).map {
        case (party, elected) => Mandate(party, elected.head.partyResult.abbreviation, elected.size)
      }.toList.sortBy(-_.seats)
    )
  }

}

case class Mandate(name: String, abbreviation: String, seats: Int)


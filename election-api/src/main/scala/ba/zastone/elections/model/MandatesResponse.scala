package ba.zastone.elections.model

import ba.zastone.elections.repos.ElectedParty


case class MandatesResponse(request: ResultsRequest, electoralUnitMandates: List[ElectoralUnitMandates]) {

  def withElectedParties(electedParties: List[ElectedParty]) = {
    copy(electoralUnitMandates =
      electedParties.groupBy(party => (party.electoralUnitId, party.electoralUnitName)).map {
        case ((electionUnitId, electionUnitName), electedPartiesForElectionUnit) =>
          ElectoralUnitMandates.fromResults(electionUnitId.value, electionUnitName,
            electedPartiesForElectionUnit)
      }.toList.sortBy(_.electoralUnitId)
    )
  }
}

object MandatesResponse {
  def fromRequest(request: ResultsRequest) = MandatesResponse(request, Nil)
}

case class ElectoralUnitMandates(electoralUnitName: Option[String], electoralUnitId: Int, mandates: List[Mandate]) {
}

object ElectoralUnitMandates {

  def fromResults(electoralUnitId: Int, electoralUnitName: Option[String],
                  electedParties: List[ElectedParty]) = {
    ElectoralUnitMandates(electoralUnitName, electoralUnitId,
      electedParties.groupBy(party => party.partyResult.name).map {
        case (party, elected) => Mandate(party, elected.size)
      }.toList.sortBy(-_.seats)
    )
  }

}

case class Mandate(name: String, seats: Int) {

}


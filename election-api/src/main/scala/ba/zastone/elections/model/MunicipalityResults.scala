package ba.zastone.elections.model


case class ResultsResponse(request: ResultsRequest, municipalityResults: MunicipalityResults)

case class ResultsRequest(electionType: String, year: Int)

case class MunicipalityResults(id: Int, name: String, electoralUnits: Seq[ElectoralUnitResults])

case class ElectoralUnitResults(id: Int, name: String)

case class PartyResults(name: String, abbreviation: String, votes: Int)

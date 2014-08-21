package ba.zastone.elections.model

import ba.zastone.elections.model.ElectionTypes.ElectionType


object ElectionTypes extends Enumeration {
  type ElectionType = Value

  val Kanton = Value("kanton")
  val ParlimentBih = Value("parliament_bih")
  val parliamentFbih = Value("parliament_fbih")
  val parliamentRs = Value("parliament_rs")
  val presidentBis = Value("president_bih")
  val presidentRs = Value("president_rs")

}

case class ResultsResponse(request: ResultsRequest, municipalityResults: MunicipalityResults)

case class ResultsRequest(electionType: ElectionType, year: Int)

case class MunicipalityResults(id: Int, name: String, electoralUnits: Seq[ElectoralUnitResults])

case class ElectoralUnitResults(id: Int, results: Seq[PartyResults])

case class PartyResults(name: String, abbreviation: String, votes: Int)

object ResultsResponse {
  val Example = ResultsResponse(
    ResultsRequest(ElectionTypes.ParlimentBih, 2010),
    MunicipalityResults(
      id = 1,
      name = "Velika Kladusa",
      electoralUnits = Seq(
        ElectoralUnitResults(
          id = 511,
          results = Seq(
            PartyResults(
              name = "Party #1",
              abbreviation = "p1",
              votes = 5
            ),
            PartyResults(
              name = "Party #2",
              abbreviation = "p2",
              votes = 2
            )
          )
        )
      )
    )
  )
}

package ba.zastone.elections.model

import ba.zastone.elections.model.ElectionTypes.ElectionType
import ba.zastone.elections.repos.ResultsTuple


object ElectionTypes extends Enumeration {
  type ElectionType = Value

  val Kanton = Value("kanton")
  val ParlimentBih = Value("parliament_bih")
  val parliamentFbih = Value("parliament_fbih")
  val parliamentRs = Value("parliament_rs")
  val presidentBis = Value("president_bih")
  val presidentRs = Value("president_rs")

}

case class ResultsResponse(request: ResultsRequest, municipalityResults: List[MunicipalityResult]) {
  def withMunicipalityResults(municipalityResult: MunicipalityResult) = {
    copy(municipalityResults = municipalityResult :: municipalityResults)
  }
}

case class ResultsRequest(electionType: ElectionType, year: Int)

case class MunicipalityResult(id: Int, name: String, electoralUnits: Seq[ElectoralUnitResults]) {

  def add(tuple: ResultsTuple): MunicipalityResult = {
    tuple.municipalityId == this.id match {
      case true => addValidated(tuple)
      case false => this
    }

  }

  private def addValidated(tuple: ResultsTuple): MunicipalityResult = {
    electoralUnits.find(euResult => euResult.id == tuple.electionUnitId) match {
      case Some(electoralUnit) =>
        copy(electoralUnits = electoralUnits.updated(electoralUnits.indexOf(electoralUnit),
          electoralUnit.add(tuple)
        ))
      case None =>
        copy(electoralUnits =
          electoralUnits :+
            ElectoralUnitResults(
              tuple.electionUnitId,
              results = Seq(PartyResults(tuple.party, tuple.abbrev, tuple.votes)
              )
            )
        )
    }
  }
}

object MunicipalityResult {
  def withoutResults(id: Int, name: String) = MunicipalityResult(id, name, Nil)
}

case class ElectoralUnitResults(id: Int, results: Seq[PartyResults]) {
  def add(tuple: ResultsTuple): ElectoralUnitResults = {
    tuple.electionUnitId == this.id match {
      case true => addValidated(tuple)
      case false => this
    }
  }

  def addValidated(tuple: ResultsTuple): ElectoralUnitResults = {
    copy(results =
      results :+
        PartyResults(tuple.party, tuple.abbrev, tuple.votes)
    )
  }

}

case class PartyResults(name: String, abbreviation: String, votes: Int)

object ResultsResponse {
  def withoutResults(request: ResultsRequest): ResultsResponse = ResultsResponse(request, Nil)

  val Example = ResultsResponse(
    ResultsRequest(ElectionTypes.ParlimentBih, 2010),
    List(
      MunicipalityResult(
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
  )
}

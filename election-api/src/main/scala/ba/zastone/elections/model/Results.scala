package ba.zastone.elections.model

import ba.zastone.elections.repos.ResultsTuple


case class ResultsResponse(request: ResultsRequest, municipalityResults: List[MunicipalityResult]) {
  def withMunicipalityResults(municipalityResult: MunicipalityResult) = {
    copy(municipalityResults = municipalityResult :: municipalityResults)
  }
}

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
              results = Seq(PartyResult(tuple.party, tuple.abbrev, tuple.votes)
              )
            )
        )
    }
  }
}

object MunicipalityResult {
  def withoutResults(id: Int, name: String) = MunicipalityResult(id, name, Nil)
}

case class ElectoralUnitResults(id: Int, results: Seq[PartyResult]) {

  def add(tuple: ResultsTuple): ElectoralUnitResults = {
    tuple.electionUnitId == this.id match {
      case true => addValidated(tuple)
      case false => this
    }
  }

  def addValidated(tuple: ResultsTuple): ElectoralUnitResults = {
    copy(results =
      results :+ PartyResult(tuple.party, tuple.abbrev, tuple.votes)
    )
  }

}


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
              PartyResult(
                name = "Party #1",
                abbreviation = "p1",
                votes = 5
              ),
              PartyResult(
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

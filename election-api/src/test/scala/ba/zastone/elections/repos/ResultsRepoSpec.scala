package ba.zastone.elections.repos

import ba.zastone.elections.infrastructure.Beans
import ba.zastone.elections.model.{ElectionTypes, Election}
import org.scalatest.{ShouldMatchers, FlatSpec}


class ResultsRepoSpec extends FlatSpec with ShouldMatchers with SQLSupport {

  val repo = {
    val beans = Beans
    beans.resultsRepo
  }

  it should "throw ElectionDataNotFound if requested data for non-existing elections" in {
    a[ElectionDataNotFound] shouldBe thrownBy {
      repo.results(Election(ElectionTypes.Kanton, 2013))
    }
  }

  it should "return a dataset for existing election" in {
    val election = Election(ElectionTypes.ParliamentRs, 2010)

    val results = repo.results(election)

    results.request shouldEqual election
    results.municipalityResults.size should (be > 0)
  }

}

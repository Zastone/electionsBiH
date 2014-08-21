package ba.zastone.elections.repos

import com.typesafe.scalalogging.slf4j.StrictLogging
import org.scalatest.{ShouldMatchers, FlatSpec}


class MunicipalityRepoSpec extends FlatSpec with ShouldMatchers with SQLSupport with StrictLogging {

  it should "deserialize municipality data (if exists)" in {
    val repo = new MunicipalitiesRepo(sqlDatabase)
    repo.findAll() match {
      case Nil => logger.warn("No entries in municipality table.")
      case head :: otherMunicipalities =>
        head.id shouldBe > (0)
    }
  }
}
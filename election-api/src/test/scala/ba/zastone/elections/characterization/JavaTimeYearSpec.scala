package ba.zastone.elections.characterization

import org.scalatest.{ShouldMatchers, FlatSpec}


class JavaTimeYearSpec extends FlatSpec with ShouldMatchers {

  it should "return the current year in a numeric format" in {
    val year = java.time.Year.now().getValue
    year should(be > 2013)
    year should(be < 2100) /* seems like a sane value */
  }

}
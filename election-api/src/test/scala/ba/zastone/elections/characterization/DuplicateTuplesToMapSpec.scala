package ba.zastone.elections.characterization

import org.scalatest.{ShouldMatchers, FlatSpec}


class DuplicateTuplesToMapSpec extends FlatSpec with ShouldMatchers {

  it should "create a map without duplicates tuples from the source list" in {
    Seq(
      (1, "a"),
      (1, "a"),
      (2, "b")
    ).toMap.size shouldEqual 2
  }
}

package ba.zastone.elections.model

import ba.zastone.elections.model.ElectionTypes.ElectionType


case class Election(electionType: ElectionType, year: Int)

object ElectionTypes extends Enumeration {
  type ElectionType = Value

  val Kanton = Value("kanton")
  val ParlimentBih = Value("parliament_bih")
  val parliamentFbih = Value("parliament_fbih")
  val parliamentRs = Value("parliament_rs")
  val presidentBis = Value("president_bih")
  val presidentRs = Value("president_rs")

}

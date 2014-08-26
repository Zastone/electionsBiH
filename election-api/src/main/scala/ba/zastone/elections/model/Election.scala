package ba.zastone.elections.model

import ba.zastone.elections.model.ElectionTypes.ElectionType

case class Election(electionType: ElectionType, year: Int)

object Election {

  import ba.zastone.elections.model.ElectionTypes._

  /**
   * List of elections for which data is available.
   *
   * This is might not be exhaustive. When 2014 data is received 
   * or previous election data is obtained this list might not be updated.
   * The purpose of it is to provide list all data that was available during the initial
   * development sprint to be used during testing. 
   *
   */
  val WithAvailableData = Seq(
    Election(ParlimentBih, 2010),
    Election(ParliamentFbih, 2010),
    Election(ParliamentRs, 2010)
  )

}

object ElectionTypes extends Enumeration {
  type ElectionType = Value

  val Kanton = Value("kanton")
  val ParlimentBih = Value("parliament_bih")
  val ParliamentFbih = Value("parliament_fbih")
  val ParliamentRs = Value("parliament_rs")
  val PresidentBih = Value("president_bih")
  val PresidentRs = Value("president_rs")

}

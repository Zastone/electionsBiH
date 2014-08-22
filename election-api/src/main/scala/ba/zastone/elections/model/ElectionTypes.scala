package ba.zastone.elections.model

/**
 * @author Maciej Bilas
 * @since 22/8/14 13:16
 */
object ElectionTypes extends Enumeration {
  type ElectionType = Value

  val Kanton = Value("kanton")
  val ParlimentBih = Value("parliament_bih")
  val parliamentFbih = Value("parliament_fbih")
  val parliamentRs = Value("parliament_rs")
  val presidentBis = Value("president_bih")
  val presidentRs = Value("president_rs")

}
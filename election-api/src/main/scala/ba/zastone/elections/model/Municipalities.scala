package ba.zastone.elections.model


case class Municipalities(municipalities: Seq[Municipality])

case class Municipality(id: Int, name: String, kanton: Option[Int], parliamentBih: Int, parliamentFbih: Option[Int],
                        parliamentRs: Option[Int], presidentBih: Option[Int], presidentRs: Option[Int])

object Municipalities {
  val Example = Municipalities(
    Seq(
      Municipality(1, "Velika Kladusa", Some(201), 511, Some(401), None, Some(701), None),
      Municipality(2, "Cazin", Some(201), 511, Some(401), None, Some(701), None)
    )
  )
}

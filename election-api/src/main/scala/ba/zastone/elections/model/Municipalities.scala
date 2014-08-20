package ba.zastone.elections.model


case class Municipalities(municipalities: Seq[Municipality])

case class Municipality(id : Int, name : String, kanton: Int, parliamentBih : Int, parliamentFbih : Int,
                        parliamentRs : Int,  presidentBih: Int, presidentRs: Int)

object Municipalities {
  val Example = Municipalities(
    Seq(Municipality(1, "Velika Kladusa", 201, 341, 451, 123, 234, 345))
  )
}

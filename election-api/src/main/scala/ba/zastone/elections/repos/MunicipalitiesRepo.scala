package ba.zastone.elections.repos

import ba.zastone.elections.model.{Municipalities, Municipality}
import ba.zastone.elections.db.SQLDatabase

import scala.slick.jdbc.StaticQuery.interpolation

class MunicipalitiesRepo(val database: SQLDatabase) {

  import database._

  def findAll() = {
    // https://issues.scala-lang.org/browse/SI-8261
    // https://github.com/slick/slick/pull/834
    val query = sql"""SELECT municipality_id, municipality_name,
           kanton, parliament_bih, parliament_fbih,
           parliament_rs, president_bih, president_rs_election FROM municipalities""".
      as[(Int, String, Option[Int], Int, Option[Int], Option[Int], Option[Int], Option[Int])]

    db.withSession { implicit session =>
      // http://slick.typesafe.com/doc/2.1.0/sql-to-slick.html#slick-plain-sql-queries
      query.list.map { e => Municipality.tupled(e)}
    }
  }

  def municipalities() = {
    Municipalities(findAll())
  }

}

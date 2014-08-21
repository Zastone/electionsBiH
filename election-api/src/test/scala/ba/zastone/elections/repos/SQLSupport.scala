package ba.zastone.elections.repos

import ba.zastone.elections.config.ElectionsConfig
import ba.zastone.elections.db.SQLDatabase
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}


/**
 * This SQLSupport trait assumes that only
 */
trait SQLSupport extends BeforeAndAfterAll with BeforeAndAfterEach {
  this: Suite =>

  protected val sqlDatabase = ConnectionHolder.sqlDatabase
}

private[repos] object ConnectionHolder {
  private val dbConfig = new ElectionsConfig
  lazy val sqlDatabase = SQLDatabase.createDb(dbConfig)

  sys.addShutdownHook {
    sqlDatabase.close()
  }

}

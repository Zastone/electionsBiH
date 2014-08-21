package ba.zastone.elections.config

import com.softwaremill.thegarden.lawn.config.ConfigWithDefaults

trait DBConfig extends ConfigWithDefaults {

  lazy val dbUsername = getString("db.username", "root")
  lazy val dbPassword = getString("db.password", "")
  lazy val dbSchema = getString("db.schema", "elections")
  lazy val dbHost = getString("db.host", "localhost")

}

package ba.zastone.elections.config

import com.softwaremill.thegarden.lawn.config.ConfigWithDefaults

trait HttpServerConfig extends ConfigWithDefaults {
  
  lazy val httpPort = getInt("http-server.port", 8080)

}

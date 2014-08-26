package ba.zastone.elections.config

import com.softwaremill.thegarden.lawn.config.HttpServerConfig
import com.typesafe.config.{ConfigFactory, Config}

trait BaseConfig {

  def rootConfig : Config
}

class ElectionsConfig extends BaseConfig with DBConfig with HttpServerConfig {

  override def rootConfig = ConfigFactory.load()

}

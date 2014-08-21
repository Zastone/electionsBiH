package ba.zastone.elections.config

import com.typesafe.config.{ConfigFactory, Config}

trait BaseConfig {

  def rootConfig : Config
}

class ElectionsConfig extends BaseConfig with DBConfig {

  override def rootConfig = ConfigFactory.load()

}
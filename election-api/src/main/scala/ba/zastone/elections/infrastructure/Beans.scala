package ba.zastone.elections.infrastructure

import akka.actor.{ActorSystem, Props}
import ba.zastone.elections.config.ElectionsConfig
import ba.zastone.elections.db.SQLDatabase
import ba.zastone.elections.metrics.MonitoringActivator
import ba.zastone.elections.repos.DaosModule
import ba.zastone.elections.web.ElectionsWebService
import com.softwaremill.macwire.Macwire
import com.softwaremill.thegarden.lawn.shutdownables._

trait ConfigModule {
  lazy val config = new ElectionsConfig
}

trait InfrastructureModule extends Macwire with ShutdownHandlerModule with ConfigModule {

  lazy val actorSystem = ActorSystem("ea-main") onShutdown { actorSystem =>
    actorSystem.shutdown()
    actorSystem.awaitTermination()
  }

  lazy val database = SQLDatabase.createDb(config) onShutdown (_.close())

  lazy val monitoringActivator = MonitoringActivator onShutdown (_.deactivate())

}

trait ElectionsModule extends Macwire with ShutdownHandlerModule with InfrastructureModule with DaosModule {

  lazy val webHandler = actorSystem.actorOf(Props(classOf[ElectionsWebService], this), "elections-service")

}

trait Beans extends ElectionsModule with DefaultShutdownHandlerModule

object Beans extends Beans with ShutdownOnJVMTermination

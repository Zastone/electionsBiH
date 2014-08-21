package ba.zastone.elections.infrastructure

import akka.actor.{Props, ActorSystem}
import ba.zastone.elections.config.ElectionsConfig
import ba.zastone.elections.repos.MunicipalitiesRepo
import ba.zastone.elections.sql.SQLDatabase
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

  lazy val database = SQLDatabase.createDb(config) onShutdown { db =>
    db.close()
  }

}

trait ReposModule extends Macwire with InfrastructureModule {

  lazy val municipalitiesRepo = wire[MunicipalitiesRepo]

}

trait ElectionsModule extends Macwire with ShutdownHandlerModule with InfrastructureModule with ReposModule {

  lazy val webHandler = actorSystem.actorOf(Props(classOf[ElectionsWebService], this), "elections-service")

}

trait Beans extends ElectionsModule with DefaultShutdownHandlerModule

object Beans extends Beans with ShutdownOnJVMTermination

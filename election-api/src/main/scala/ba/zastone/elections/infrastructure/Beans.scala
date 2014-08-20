package ba.zastone.elections.infrastructure

import akka.actor.{Props, ActorSystem}
import ba.zastone.elections.web.ElectionsWebService
import com.softwaremill.macwire.Macwire
import com.softwaremill.thegarden.lawn.shutdownables._

trait ElectionsModule extends Macwire with ShutdownHandlerModule {

  lazy val actorSystem = ActorSystem("ea-main") onShutdown { actorSystem =>
    actorSystem.shutdown()
    actorSystem.awaitTermination()
  }

  lazy val webHandler = actorSystem.actorOf(Props(classOf[ElectionsWebService], this), "elections-service")
}

trait Beans extends ElectionsModule with DefaultShutdownHandlerModule

object Beans extends Beans with ShutdownOnJVMTermination


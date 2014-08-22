package ba.zastone.elections.web

import akka.io.IO
import ba.zastone.elections.infrastructure.Beans
import com.typesafe.scalalogging.slf4j.StrictLogging
import spray.can.Http
import spray.routing.SimpleRoutingApp

object ElectionsWeb extends App with SimpleRoutingApp with StrictLogging {

  val beans = Beans
  implicit val system = beans.actorSystem

  beans.monitoringActivator.activate()

  IO(Http) ! Http.Bind(beans.webHandler, interface = "0.0.0.0", port = beans.config.httpPort)

}

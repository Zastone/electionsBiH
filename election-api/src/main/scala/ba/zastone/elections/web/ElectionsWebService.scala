package ba.zastone.elections.web

import akka.actor.Actor
import ba.zastone.elections.infrastructure.Beans


class ElectionsWebService(beans : Beans) extends Actor with ElectionsService {
  override implicit def actorRefFactory = context

  override def receive = runRoute(electionsRoute)

}

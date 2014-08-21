package ba.zastone.elections.web

import akka.actor.Actor
import ba.zastone.elections.infrastructure.Beans
import ba.zastone.elections.repos.MunicipalitiesRepo


class ElectionsWebService(beans : Beans) extends Actor with ElectionsService {
  override implicit def actorRefFactory = context

  override def receive = runRoute(electionsRoute)

  override protected val municipalityRepo: MunicipalitiesRepo = beans.municipalitiesRepo
}

package ba.zastone.elections.repos

import ba.zastone.elections.infrastructure.InfrastructureModule
import com.softwaremill.macwire.Macwire

trait ReposModule extends Macwire with InfrastructureModule {

  lazy val municipalitiesRepo = wire[MunicipalitiesRepo]

  lazy val resultsRepo = wire[ResultsRepo]

  lazy val mandatesDao = wire[MandatesDao]
  lazy val mandatesService = wire[MandatesService]

}

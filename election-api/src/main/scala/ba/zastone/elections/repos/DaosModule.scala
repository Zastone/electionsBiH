package ba.zastone.elections.repos

import ba.zastone.elections.infrastructure.InfrastructureModule
import com.softwaremill.macwire.Macwire

trait DaosModule extends Macwire with InfrastructureModule {

  lazy val municipalitiesRepo = wire[MunicipalitiesRepo]

  lazy val resultsRepo = wire[ResultsRepo]

  lazy val parliamentSeatsDao = wire[ParliamentSeatsDao]

  lazy val mandatesDao = wire[MandatesDao]
  lazy val mandatesService = wire[MandatesService]

}

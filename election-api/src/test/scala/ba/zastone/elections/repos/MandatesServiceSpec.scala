package ba.zastone.elections.repos

import ba.zastone.elections.infrastructure.Beans
import ba.zastone.elections.model.Election
import org.scalatest.{ShouldMatchers, FlatSpec}


class MandatesServiceSpec extends FlatSpec with ShouldMatchers with SQLSupport {

  behavior of classOf[MandatesService].getSimpleName

  val beans = Beans

  val mandatesService = beans.mandatesService
  val mandatesDao = beans.mandatesDao

  for (election <- Election.WithAvailableData) {
    val electoralUnitMandatesList = mandatesService.mandates(election).electoralUnitMandates
    val availableSeats = mandatesDao.seatCountsByElectionUnitId(election)

    for (electoralUnitMandates <- electoralUnitMandatesList) {
      val electoralUnitId = electoralUnitMandates.electoralUnitId
      it should s"compute mandates for all available seats for $election and electoral unit $electoralUnitId" in {
        electoralUnitMandates.mandatesSum() shouldEqual availableSeats(electoralUnitId).seats
      }
    }

    it should s"compute mandates for all electoral units in $election" in {
      electoralUnitMandatesList.foldLeft(availableSeats) {
        case (seats, electoralUnitMandates) =>
          seats - electoralUnitMandates.electoralUnitId
      } shouldBe 'empty
    }
  }
}

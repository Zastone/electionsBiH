package ba.zastone.elections.model

import ba.zastone.elections.model.BiHEntities.BiHEntity
import ba.zastone.elections.model.ElectionTypes.ElectionType

object BiHEntities extends Enumeration {
  val FBiH = Value("fbih")
  val RS = Value("rs")

  type BiHEntity = Value
}

case class ElectionUnitId(value: Int) extends AnyVal with Ordered[ElectionUnitId] {
  override def compare(that: ElectionUnitId) = this.value.compare(that.value)
}

case class ElectoralUnit(electionType: ElectionType, electionUnitId: ElectionUnitId, seats: Int,
                         compensationUnitIdOpt: Option[ElectionUnitId], compensatory: Boolean) {

  val isCompensatory = compensatory && compensationUnitIdOpt.isEmpty

  val compensatoryElectionUnitId = ElectionUnitId(electionUnitId.value / 100 * 100) /* integer math */

}

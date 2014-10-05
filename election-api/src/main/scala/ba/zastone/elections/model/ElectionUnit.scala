package ba.zastone.elections.model

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
                         compensatoryUnitIdOpt: Option[ElectionUnitId], compensatory: Boolean) {

  val isCompensatory = compensatory && compensatoryUnitIdOpt.isEmpty

  def compensatoryElectionUnitId : ElectionUnitId = compensatoryUnitIdOpt match {
    case None => ElectionUnitId(-1) /* fallback for old code */
    case Some(compensatoryUnitId) => compensatoryUnitId
  }

}

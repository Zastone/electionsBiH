package ba.zastone.elections.model

import ba.zastone.elections.model.ElectionTypes.ElectionType


case class Election(electionType: ElectionType, year: Int)
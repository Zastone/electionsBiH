package ba.zastone.elections.model

import ba.zastone.elections.model.ElectionTypes.ElectionType


case class ResultsRequest(electionType: ElectionType, year: Int)
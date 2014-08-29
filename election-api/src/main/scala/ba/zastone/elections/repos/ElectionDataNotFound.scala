package ba.zastone.elections.repos

import ba.zastone.elections.model.Election


class ElectionDataNotFound(val election : Election) extends Exception

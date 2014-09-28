package ba.zastone.elections.cli

import ba.zastone.elections.infrastructure.Beans
import ba.zastone.elections.model.{Election, ElectionTypes}


object ResultsPrinter extends App {
  import org.json4s.jackson.Serialization.writePretty

  import ba.zastone.elections.api.JsonFormats

  val resultsRepo = Beans.resultsRepo

  val results = resultsRepo.results(Election(ElectionTypes.ParlimentBih, 2010))

  println(writePretty(results))
}

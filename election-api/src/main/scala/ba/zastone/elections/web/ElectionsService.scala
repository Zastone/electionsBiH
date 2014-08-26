package ba.zastone.elections.web

import java.text.SimpleDateFormat
import java.util.Locale

import ba.zastone.elections.model.{MandatesResponse, ResultsResponse, Election, ElectionTypes}
import ba.zastone.elections.repos.{MandatesService, MunicipalitiesRepo, ResultsRepo}
import com.softwaremill.thegarden.json4s.serializers.UnderscorizeFieldNamesSerializer
import com.softwaremill.thegarden.spray.directives.CorsSupport
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.json4s.DefaultFormats
import org.json4s.ext.{EnumNameSerializer, JodaTimeSerializers}
import spray.caching.{Cache, LruCache}
import spray.httpx.Json4sJacksonSupport
import spray.httpx.encoding.{Deflate, Gzip, NoEncoding}
import spray.routing.HttpService

trait ElectionsService extends HttpService with Json4sJacksonSupport with LazyLogging with CorsSupport {
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  protected val municipalityRepo: MunicipalitiesRepo

  protected val resultsRepo: ResultsRepo

  protected val mandatesService: MandatesService

  implicit def json4sJacksonFormats = new DefaultFormats {
    override protected def dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
  } ++ JodaTimeSerializers.all +
    UnderscorizeFieldNamesSerializer +
    new EnumNameSerializer(ElectionTypes)

  val resultsCache: Cache[ResultsResponse] = LruCache(timeToLive = 10.minutes)
  val mandatesCache: Cache[MandatesResponse] = LruCache(timeToLive = 10.minutes)

  protected def apiCompressResponse = compressResponse(Gzip, Deflate, NoEncoding)

  protected def municipalitiesRoute = path("municipalities") {
    get {
      logRequest("municipalities") {
        complete {
          municipalityRepo.municipalities()
        }
      }
    }
  }

  protected def resultsRoute = path("results" / Segment / IntNumber) { (electionTypeStr, year) =>
    get {
      apiCompressResponse {
        logRequest("results") {
          complete {
            val election = Election(ElectionTypes.withName(electionTypeStr), year)
            resultsCache(election) {
              resultsRepo.results(ElectionTypes.withName(electionTypeStr), year)
            }
          }
        }
      }
    }
  }

  protected def mandatesRoute = path("mandates" / Segment / IntNumber) { (electionTypeStr, year) =>
    get {
      apiCompressResponse {
        logRequest("mandates") {
          complete {
            val election = Election(ElectionTypes.withName(electionTypeStr), year)
            mandatesCache(election) {
              mandatesService.mandates(election)
            }
          }
        }
      }
    }
  }

  protected def electionsRoute = cors {
    pathPrefix("v1") {
      municipalitiesRoute ~ resultsRoute ~ mandatesRoute
    }
  }

}

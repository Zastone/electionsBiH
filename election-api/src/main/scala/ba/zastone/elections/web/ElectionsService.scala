package ba.zastone.elections.web

import java.text.SimpleDateFormat
import java.util.Locale

import ba.zastone.elections.cache.ElectionCache
import ba.zastone.elections.model.{Election, ElectionTypes, MandatesResponse, ResultsResponse}
import ba.zastone.elections.repos.{ElectionDataNotFound, MandatesService, MunicipalitiesRepo, ResultsRepo}
import com.softwaremill.thegarden.json4s.serializers.UnderscorizeFieldNamesSerializer
import com.softwaremill.thegarden.spray.directives.CorsSupport
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.json4s.DefaultFormats
import org.json4s.ext.{EnumNameSerializer, JodaTimeSerializers}
import spray.caching.Cache
import spray.http.StatusCodes
import spray.httpx.Json4sJacksonSupport
import spray.httpx.encoding.{Deflate, Gzip, NoEncoding}
import spray.routing.{ExceptionHandler, HttpService}
import spray.routing.directives.DetachMagnet

trait ElectionsService extends HttpService with Json4sJacksonSupport with LazyLogging with CorsSupport {

  protected val municipalityRepo: MunicipalitiesRepo

  protected val resultsRepo: ResultsRepo

  protected val mandatesService: MandatesService

  implicit def json4sJacksonFormats = new DefaultFormats {
    override protected def dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
  } ++ JodaTimeSerializers.all +
    UnderscorizeFieldNamesSerializer +
    new EnumNameSerializer(ElectionTypes)

  val resultsCache: Cache[ResultsResponse] = ElectionCache()
  val mandatesCache: Cache[MandatesResponse] = ElectionCache()

  val electionDataNotFoundHandler: ExceptionHandler = ExceptionHandler {
    case e: ElectionDataNotFound =>
      complete {
        StatusCodes.NotFound -> e.election
      }
  }

  import scala.concurrent.ExecutionContext.Implicits.global

  protected def apiCompressResponse = compressResponse(Gzip, Deflate, NoEncoding)

  protected def municipalitiesRoute = path("municipalities") {
    get {
      detach(new DetachMagnet()) {
        complete {
          municipalityRepo.municipalities()
        }
      }
    }
  }

  protected def resultsRoute = path("results" / Segment / IntNumber) {
    (electionTypeStr, year) =>
      get {
        complete {
          val election = Election(ElectionTypes.withName(electionTypeStr), year)
          resultsCache(election) {
            resultsRepo.results(election)
          }
        }
      }
  }

  protected def mandatesRoute = path("mandates" / Segment / IntNumber) {
    (electionTypeStr, year) =>
      get {
        complete {
          val election = Election(ElectionTypes.withName(electionTypeStr), year)
          mandatesCache(election) {
            mandatesService.mandates(election)
          }
        }
      }
  }

  protected def electionsRoute =
    pathPrefix("v1") {
      cors {
        apiCompressResponse {
          logRequest("API") {
            municipalitiesRoute ~
              handleExceptions(electionDataNotFoundHandler) {
                resultsRoute ~ mandatesRoute
              }
          }
        }
      }
    }

}

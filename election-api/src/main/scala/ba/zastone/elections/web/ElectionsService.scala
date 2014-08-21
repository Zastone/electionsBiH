package ba.zastone.elections.web

import java.text.SimpleDateFormat
import java.util.Locale

import ba.zastone.elections.model.ElectionTypes
import ba.zastone.elections.model.ResultsResponse
import ba.zastone.elections.repos.MunicipalitiesRepo
import com.softwaremill.thegarden.json4s.serializers.UnderscorizeFieldNamesSerializer
import org.json4s.DefaultFormats
import org.json4s.ext.{EnumNameSerializer, JodaTimeSerializers}
import spray.httpx.Json4sJacksonSupport
import spray.httpx.encoding.{Deflate, NoEncoding, Gzip}
import spray.routing.HttpService


trait ElectionsService extends HttpService with Json4sJacksonSupport {

  protected val municipalityRepo: MunicipalitiesRepo

  implicit def json4sJacksonFormats = new DefaultFormats {
    override protected def dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
  } ++ JodaTimeSerializers.all +
    UnderscorizeFieldNamesSerializer +
    new EnumNameSerializer(ElectionTypes)

  protected def apiCompressResponse = compressResponse(Gzip, Deflate, NoEncoding)

  protected def municipalitiesRoute = path("municipalities") {
    get {
      complete {
        municipalityRepo.municipalities()
      }
    }
  }

  protected def resultsRoute = path("results" / Segment / IntNumber) { (electionTypeStr, year) =>
    get {
      apiCompressResponse {
        complete {
          ResultsResponse.Example
        }
      }
    }
  }

  protected def mandatesRoute = path("mandates" / Segment / IntNumber) { (electionTypeStr, year) =>
    get {
      apiCompressResponse {
        complete {
          "1"
        }
      }
    }
  }

  protected def electionsRoute = municipalitiesRoute ~ resultsRoute
}

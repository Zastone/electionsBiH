package ba.zastone.elections.web

import java.text.SimpleDateFormat
import java.util.Locale

import ba.zastone.elections.repos.MunicipalitiesRepo
import com.softwaremill.thegarden.json4s.serializers.UnderscorizeFieldNamesSerializer
import org.json4s.DefaultFormats
import org.json4s.ext.JodaTimeSerializers
import spray.httpx.Json4sJacksonSupport
import spray.routing.HttpService


trait ElectionsService extends HttpService with Json4sJacksonSupport {

  protected val municipalityRepo : MunicipalitiesRepo

  implicit def json4sJacksonFormats = new DefaultFormats {
    override protected def dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
  } ++ JodaTimeSerializers.all + UnderscorizeFieldNamesSerializer

  protected def municipalitiesRoute = path("municipalities") {
    get {
      complete {
        municipalityRepo.municipalities()
      }
    }
  }

  protected def electionsRoute = municipalitiesRoute
}

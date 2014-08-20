package ba.zastone.elections.web

import java.text.SimpleDateFormat
import java.util.Locale

import ba.zastone.elections.model.Municipalities
import org.json4s.DefaultFormats
import org.json4s.ext.JodaTimeSerializers
import spray.httpx.Json4sJacksonSupport
import spray.routing.HttpService


trait ElectionsService extends HttpService with Json4sJacksonSupport {

  implicit def json4sJacksonFormats = new DefaultFormats {
    override protected def dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
  } ++ JodaTimeSerializers.all

  protected def municipalitiesRoute = path("municipalities") {
    get {
      complete{
        Municipalities.Example
      }
    }
  }

  protected def electionsRoute = municipalitiesRoute
}

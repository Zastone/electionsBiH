package ba.zastone.elections.web

import java.text.SimpleDateFormat
import java.util.Locale

import ba.zastone.elections.model.{Municipality, Municipalities}
import org.json4s.{FieldSerializer, DefaultFormats}
import org.json4s.ext.JodaTimeSerializers
import spray.httpx.Json4sJacksonSupport
import spray.routing.HttpService


trait ElectionsService extends HttpService with Json4sJacksonSupport {

  implicit def json4sJacksonFormats = new DefaultFormats {
    override protected def dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
  } ++ JodaTimeSerializers.all +
  FieldSerializer[Municipality](serializer = {
    case (name, v) => Some((Snakize.transform(name), v))
  })

  protected def municipalitiesRoute = path("municipalities") {
    get {
      complete {
        Municipalities.Example
      }
    }
  }

  protected def electionsRoute = municipalitiesRoute
}

private[web] object Snakize {
  def transform(word: String): String = {
    val spacesPattern = "[-\\s]".r
    val firstPattern = "([A-Z]+)([A-Z][a-z])".r
    val secondPattern = "([a-z\\d])([A-Z])".r
    val replacementPattern = "$1_$2"
    spacesPattern.replaceAllIn(
      secondPattern.replaceAllIn(
        firstPattern.replaceAllIn(
          word, replacementPattern), replacementPattern), "_").toLowerCase
  }
}

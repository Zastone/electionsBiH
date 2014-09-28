package ba.zastone.elections

import java.text.SimpleDateFormat
import java.util.Locale

import ba.zastone.elections.model.ElectionTypes
import com.softwaremill.thegarden.json4s.serializers.UnderscorizeFieldNamesSerializer
import org.json4s.DefaultFormats
import org.json4s.ext.{EnumNameSerializer, JodaTimeSerializers}

/**
 * @author Maciej Bilas
 * @since 28/9/14 15:02
 */
package object api {
  implicit val JsonFormats = new DefaultFormats {
    override protected def dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
  } ++ JodaTimeSerializers.all +
    UnderscorizeFieldNamesSerializer +
    new EnumNameSerializer(ElectionTypes)
}

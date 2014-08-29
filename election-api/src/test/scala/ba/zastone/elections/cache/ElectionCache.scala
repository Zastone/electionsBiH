package ba.zastone.elections.cache

import java.time.Year

import ba.zastone.elections.model.Election
import com.typesafe.scalalogging.slf4j.LazyLogging


object ElectionCache extends LazyLogging {
  private def currentYear = Year.now().getValue

  val ElectionDiscriminator = (untypedElection : Any) =>
    untypedElection match {
      case Election(_, year) if year == currentYear => CacheGenerations.ShortLived
      case Election(_, year) if year != currentYear => CacheGenerations.LongLived
      case _ =>
        logger.warn(s"Detected key other than type Election. " +
          s"(class: ${untypedElection.getClass.getCanonicalName}}, " +
          s"toString: ${untypedElection.toString}})")
        CacheGenerations.ShortLived
    }
}

class ElectionCache[V] extends TwoGenConditionalCache[V](ElectionCache.ElectionDiscriminator)

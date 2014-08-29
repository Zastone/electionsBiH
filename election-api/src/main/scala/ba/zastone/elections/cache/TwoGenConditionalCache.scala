package ba.zastone.elections.cache

import spray.caching.{LruCache, Cache}

import scala.concurrent.{ExecutionContext, Future}
import ba.zastone.elections.cache.CacheGenerations.CacheGeneration

object CacheGenerations extends Enumeration {
  val ShortLived = Value
  val LongLived = Value

  type CacheGeneration = Value
}


class TwoGenConditionalCache[V](genDiscriminator: (Any) => CacheGeneration) extends Cache[V] {

  import scala.concurrent.duration._

  import CacheGenerations._

  private val shortLived = LruCache[V](timeToLive = 10.minutes)
  private val longLived = LruCache[V](timeToLive = 24.hours)

  private val caches = Seq(shortLived, longLived)

  private def getCache(key: Any) = {
    genDiscriminator(key) match {
      case ShortLived => shortLived
      case LongLived => longLived
    }
  }

  override def apply(key: Any, genValue: () => Future[V])(implicit ec: ExecutionContext) =
    getCache(key).apply(key, genValue)

  override def get(key: Any) = getCache(key).get(key)

  override def clear() = caches.foreach(_.clear())

  override def size = shortLived.size + longLived.size

  override def remove(key: Any) = getCache(key).remove(key)

}

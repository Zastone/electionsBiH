package ba.zastone.elections.cache

import org.scalatest.concurrent.AsyncAssertions
import org.scalatest.{FlatSpec, ShouldMatchers}

import scala.concurrent.Future

class TwoGenConditionalCacheSpec extends FlatSpec with ShouldMatchers with AsyncAssertions {

  val w = new Waiter

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  private val alwaysShortLivedDiscriminator = { key: Any =>
    CacheGenerations.ShortLived
  }

  private val positiveIntIsLongLivedDiscriminator = { key: Any =>
    key match {
      case v: Int if v > 0 => CacheGenerations.LongLived
      case _ => CacheGenerations.ShortLived
    }
  }

  private def emptyCache =
    new TwoGenConditionalCache[Int](alwaysShortLivedDiscriminator)

  private def cacheWithValuesInTwoInternalCaches = {
    val c = new TwoGenConditionalCache[Int](positiveIntIsLongLivedDiscriminator)
    c(1, () => Future(1))
    c(-1, () => Future(-1))
    c
  }

  behavior of classOf[TwoGenConditionalCache[Int]].getSimpleName

  it should "return 0 as the size of both internal caches if they are empty" in {
    emptyCache.size shouldEqual 0
  }

  it should "return the sum of sizes of both caches" in {
    cacheWithValuesInTwoInternalCaches.size shouldEqual 2
  }

  it should "clear the both internal caches if requested" in {
    val cache = cacheWithValuesInTwoInternalCaches
    cache.clear()
    cache.size shouldEqual 0
  }

  it should "use the discriminator" in {
    var shortLivedCounterCalls = 0
    var longLivedCounterCalls = 0

    val countingDiscriminator = { key: Any =>
      key match {
        case v: Int if v > 0 =>
          longLivedCounterCalls += 1
          CacheGenerations.LongLived
        case _ =>
          shortLivedCounterCalls += 1
          CacheGenerations.ShortLived
      }
    }
    val c = new TwoGenConditionalCache[Int](countingDiscriminator)
    c(1, () => Future(1))
    c(-1, () => Future(-1))

    shortLivedCounterCalls shouldEqual 1
    longLivedCounterCalls shouldEqual 1
  }

  it should "actually return cached values" in {
    val cachedValue = 1
    val newlyComputedValue = 2

    cacheWithValuesInTwoInternalCaches.apply(1, () => Future(newlyComputedValue))
      .onSuccess { case v: Int =>
      w(v shouldEqual cachedValue)
      w.dismiss()
    }
    w.await(timeout(300.millis))
  }

}
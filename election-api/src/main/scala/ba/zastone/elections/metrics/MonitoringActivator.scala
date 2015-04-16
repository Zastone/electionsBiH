package ba.zastone.elections.metrics

import java.util.concurrent.TimeUnit

import com.codahale.metrics.Slf4jReporter
import org.slf4j.LoggerFactory

object MonitoringActivator {

  private lazy val logReporter = Slf4jReporter.forRegistry(MetricRegistryHolder.metricRegistry)
    .outputTo(LoggerFactory.getLogger("ba.zastone.elections.metrics"))
    .convertRatesTo(TimeUnit.SECONDS)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .build()

  // Oh, this is so ugly... fix it!
  private def runningOnDevBox() = sys.props("user.name") == "maciejb"

  def activate(): Unit = {
    if (!runningOnDevBox()) {
      logReporter.start(10, TimeUnit.SECONDS)
    }
  }

  def deactivate(): Unit = {
    if (!runningOnDevBox()) {
      logReporter.stop()
    }
  }

}

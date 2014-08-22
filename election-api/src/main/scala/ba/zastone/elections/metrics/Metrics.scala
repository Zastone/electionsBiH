package ba.zastone.elections.metrics

import nl.grons.metrics.scala.InstrumentedBuilder
import com.codahale.metrics.MetricRegistry


trait Metrics extends InstrumentedBuilder {
  val metricRegistry = MetricRegistryHolder.metricRegistry
}

object MetricRegistryHolder {
  lazy val metricRegistry = new MetricRegistry
}

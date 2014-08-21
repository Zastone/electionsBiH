package ba.zastone.elections.sql

import java.sql.Time
import javax.sql.DataSource

import ba.zastone.elections.config.DBConfig
import com.mchange.v2.c3p0.{ComboPooledDataSource, DataSources}
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.joda.time.{DateTime, DateTimeZone, LocalDateTime, LocalTime}

import scala.slick.driver.JdbcProfile
import scala.slick.jdbc.JdbcBackend._


case class SQLDatabase(db: scala.slick.jdbc.JdbcBackend.Database,
                       driver: JdbcProfile,
                       ds: DataSource) extends LazyLogging {

  import driver.simple._

  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, java.sql.Timestamp](
    dt => new java.sql.Timestamp(dt.getMillis),
    t => new DateTime(t.getTime).withZone(DateTimeZone.UTC)
  )

  implicit val localTimeColumnType = MappedColumnType.base[LocalTime, java.sql.Time](
    dt => new Time(LocalDateTime.now().
      withTime(dt.getHourOfDay, dt.getMinuteOfHour, dt.getSecondOfMinute, dt.getMillisOfSecond).toDate.getTime),
    t => new LocalDateTime(t.getTime).toLocalTime
  )

  def close() {
    DataSources.destroy(ds)
  }
}

object SQLDatabase extends LazyLogging {
  private def connectionString(config: DBConfig): String = {
    import config._
    s"jdbc:mysql://$dbHost:3306/$dbSchema"
  }

  def createDb(config: DBConfig): SQLDatabase = {
    val ds = createConnectionPool(connectionString(config))
    ds.setUser(config.dbUsername)
    ds.setPassword(config.dbPassword)

    val db = Database.forDataSource(ds)

    SQLDatabase(db, scala.slick.driver.MySQLDriver, ds)
  }

  private def createConnectionPool(connectionString: String) = {
    val cpds = new ComboPooledDataSource()
    cpds.setDriverClass("com.mysql.jdbc.Driver")
    cpds.setJdbcUrl(connectionString)
    cpds
  }
}
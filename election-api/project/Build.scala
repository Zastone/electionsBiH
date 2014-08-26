import sbt.Keys._
import sbt._

// https://github.com/sbt/sbt-assembly
import sbtassembly.Plugin._
import AssemblyKeys._


object Resolvers {
  val customResolvers = Seq(
    "SoftwareMill Public Releases" at "https://nexus.softwaremill.com/content/repositories/releases/",
    "SoftwareMill Public Snapshots" at "https://nexus.softwaremill.com/content/repositories/snapshots/",
    "spray" at "http://repo.spray.io/"
  )
}

object Dependencies {
  private val slf4jVersion = "1.7.6"
  val logging = Seq(
    "org.slf4j" % "slf4j-api" % slf4jVersion,
    "org.slf4j" % "log4j-over-slf4j" % slf4jVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.1",
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
  )

  val macwireVersion = "0.7"
  val macwire = Seq(
    "com.softwaremill.macwire" %% "macros" % macwireVersion,
    "com.softwaremill.macwire" %% "runtime" % macwireVersion
  )

  val sprayVersion = "1.3.1"
  val spray = Seq(
    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-caching" % sprayVersion,
    "io.spray" %% "spray-testkit" % sprayVersion % "test"
  )

  val json4sVersion = "3.2.10"
  val json4s = "org.json4s" %% "json4s-jackson" % json4sVersion
  val json4sExt = "org.json4s" %% "json4s-ext" % json4sVersion

  val httpStack = Seq(
    json4s,
    json4sExt
  ) ++ spray


  val gardenVersion = "0.0.20-SNAPSHOT"
  val garden = "com.softwaremill.thegarden" %% "shrubs" % gardenVersion % "test" ::
    List("garden-spray", "lawn", "garden-json4s").map("com.softwaremill.thegarden" %% _ % gardenVersion)

  val akkaVersion = "2.3.5"
  val akkaActors = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val akka = Seq(akkaActors, akkaSlf4j, akkaTestKit)

  val mysqlDriver = "mysql" % "mysql-connector-java" % "5.1.32"
  val c3p0 = "com.mchange" % "c3p0" % "0.9.5-pre6"

  val slick = "com.typesafe.slick" %% "slick" % "2.1.0"

  val dbStack = Seq(mysqlDriver, c3p0, slick)

  val scalaTest = "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"
  val mockito = "org.mockito" % "mockito-all" % "1.9.5" % "test"

  val testingStack = Seq(scalaTest, mockito)

  val metricsVersion = "3.0.1"
  val metricsCore = "com.codahale.metrics" % "metrics-core" % metricsVersion
  val metricsGraphite = "com.codahale.metrics" % "metrics-graphite" % metricsVersion
  // Project page: https://github.com/erikvanoosten/metrics-scala
  val metricsScala = "nl.grons" %% "metrics-scala" % "3.2.0_a2.3"

  /* We don't report to graphite yet */
  val metrics = Seq(metricsCore, metricsScala)

  val commonDependencies = logging ++ macwire ++ httpStack ++ garden ++ akka ++ dbStack ++ testingStack ++ metrics

}

object ElectionApiBuild extends Build {

  import Dependencies._

  override val settings = super.settings ++ Seq(
    name := "elections-api",
    version := "1.0",
    scalaVersion := "2.11.2",
    scalacOptions in GlobalScope in Compile := Seq("-unchecked", "-deprecation", "-feature"),
    scalacOptions in GlobalScope in Test := Seq("-unchecked", "-deprecation", "-feature"),
    organization := "ba.zastone.elections"
  )

  lazy val slf4jExclusionHack = Seq(
    ivyXML :=
      <dependencies>
        <exclude org="org.slf4j" artifact="slf4j-log4j12"/>
        <exclude org="log4j" artifact="log4j"/>
      </dependencies>
  )

  // Removed Project.defaultSettings as compared to prior boilerplate versions
  // see the thread why it got deprecated: https://groups.google.com/forum/#!topic/scala-tools/uuFsGN1DhNs

  lazy val commonSettings = Seq(isSnapshot <<= isSnapshot or version(_ endsWith "-SNAPSHOT")) ++ slf4jExclusionHack ++
    Seq(
      resolvers ++= Resolvers.customResolvers
    ) ++
    Seq(
      mainClass in assembly := Some("ba.zastone.elections.web.ElectionsApiWeb")
    ) ++ assemblySettings

  lazy val root = Project(
    id = "elections-api",
    base = file("."),
    settings = commonSettings
  ).settings(
      libraryDependencies ++= commonDependencies,
      // HACK
      /*
       * I couldn't get this directory to be mapped to webapp, so we have a structure web/webapp
       * Here are some links where to start researching this topic:
       * http://www.scala-sbt.org/release/docs/Howto/defaultpaths.html
       * http://www.scala-sbt.org/release/docs/Detailed-Topics/Paths.html
       * http://www.scala-sbt.org/release/docs/Detailed-Topics/Mapping-Files.html
       */
      unmanagedResourceDirectories in Compile <++= baseDirectory {
        base =>
          Seq(base / "web")
      }
    )
}
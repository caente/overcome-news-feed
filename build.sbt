import sbtassembly.Plugin._
import sbtassembly.Plugin.AssemblyKeys._

organization := "com.dimeder"

version := "0.1"

scalaVersion := "2.10.3"

name := "overcome-news-feed"


assemblySettings

mainClass in (Compile, run) := Some("com.dimeder.Main")

mainClass in assembly := Some("com.dimeder.Main")


libraryDependencies ++= {
  val akkaV = "2.2.3"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "org.twitter4j" % "twitter4j-core" % "3.0.5",
    "org.twitter4j" % "twitter4j-stream" % "3.0.5",
    "org.specs2" %% "specs2" % "1.14" % "test",
    "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test",
    "com.netflix.rxjava" % "rxjava-scala" % "0.16.1",
    "org.mongodb" %% "casbah" % "2.6.3",
    "ch.qos.logback" % "logback-classic" % "1.0.9"
  )
}

//seq(Revolver.settings: _*)

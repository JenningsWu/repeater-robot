name := "repeater-robot"

version := "0.1"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "com.github.etaty" %% "rediscala" % "1.8.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.roundeights" %% "hasher" % "1.2.0",
  "info.mukel" %% "telegrambot4s" % "3.0.9"
)
name := "hello_cats_based_lib"

version := "0.1"

scalaVersion := "2.13.1"

lazy val loggingLibraries = Seq(
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "net.logstash.logback" % "logstash-logback-encoder" % "6.1",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.9"
)

val catsVersion = "2.1.0"
val catsEffectVersion = "2.0.0"
val circeVersion = "0.12.2"
val circeConfigVersion = "0.7.0"
val http4sVersion = "0.21.0-M6"
val doobieVersion = "0.8.8"
val mysqlVersion = "8.0.16"
val logbackVersion = "1.2.3"
val jacksonScalaVersion = "2.9.9"
val scalaTestVersion = "3.0.8"
val mockitoScalaVersion = "1.5.14"
val log4catsVersion = "1.0.1"
val redis4catsVersion = "0.8.3"

lazy val common = Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonScalaVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "org.mockito" %% "mockito-scala" % mockitoScalaVersion % Test,
  "org.mockito" %% "mockito-scala-scalatest" % mockitoScalaVersion % Test
)

lazy val database = Seq(
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-hikari" % doobieVersion,
  "org.tpolecat" %% "doobie-scalatest" % doobieVersion % Test,
  "mysql" % "mysql-connector-java" % mysqlVersion
)

lazy val redis = Seq(
  "io.chrisdavenport" %% "log4cats-core" % log4catsVersion,
  "io.chrisdavenport" %% "log4cats-slf4j" % log4catsVersion,
  "dev.profunktor" %% "redis4cats-effects" % redis4catsVersion,
  "dev.profunktor" %% "redis4cats-log4cats" % redis4catsVersion
)

lazy val circe = Seq(
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-literal" % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-config" % circeConfigVersion
)

lazy val httpClient = Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion
)

lazy val httpServer = Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion
)

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.9.2",
  "org.joda" % "joda-convert" % "1.8",
  "com.h2database" % "h2" % "1.4.+",
  "org.typelevel" %% "cats-core" % "1.6.0",
  "io.monix" %% "monix" % "3.1.0",
  "net.ruippeixotog" %% "scala-scraper" % "2.2.0"
) ++ loggingLibraries ++ common ++ database ++ redis ++ circe ++ httpClient ++ httpServer

addCompilerPlugin(("org.typelevel" % "kind-projector" % "0.11.0").cross(CrossVersion.full))

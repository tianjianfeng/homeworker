error id: local223
file://<WORKSPACE>/build.sbt
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol local223
|empty definition using fallback
non-local guesses:
	 -

Document text:

```scala
val scala3Version = "3.6.3"
val http4sVersion = "0.23.25"
val circeVersion = "0.14.6"
val doobieVersion = "1.0.0-RC4"
val catsEffectVersion = "3.5.7"
val pureConfigVersion = "0.17.6"
val log4catsVersion = "2.7.0"
val flywayVersion = "9.22.3"
val tapirVersion = "1.9.10"
val prometheusVersion = "0.16.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "homeworker",
    version := "0.1.0",
    scalaVersion := "3.6.3",
    
    // Add fork setting
    Compile / run / fork := true,
    
    // Assembly settings
    assembly / assemblyJarName := "homeworker.jar",
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case "application.conf" => MergeStrategy.concat
      case "reference.conf" => MergeStrategy.concat
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    },

    libraryDependencies ++= Seq(
      // HTTP4s
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-server" % http4sVersion,
      
      // Circe
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      
      // Doobie
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
      
      // Cats Effect
      "org.typelevel" %% "cats-effect" % "3.5.3",
      
      // Config
      "com.github.pureconfig" %% "pureconfig-core" % pureConfigVersion,
      
      // Logging
      "org.typelevel" %% "log4cats-slf4j" % log4catsVersion,
      "ch.qos.logback" % "logback-classic" % "1.4.14",
      
      // Flyway
      "org.flywaydb" % "flyway-core" % flywayVersion,
      "org.flywaydb" % "flyway-database-postgresql" % flywayVersion,
      
      // Tapir
      "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
      
      // Metrics & Monitoring
      "io.prometheus" % "simpleclient" % prometheusVersion,
      "io.prometheus" % "simpleclient_hotspot" % prometheusVersion,
      "io.prometheus" % "simpleclient_common" % prometheusVersion
    )
  ) 
```

#### Short summary: 

empty definition using pc, found symbol in pc: 
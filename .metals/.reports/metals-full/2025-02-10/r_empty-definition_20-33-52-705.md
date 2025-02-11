error id: `<none>`.
file://<WORKSPACE>/build.sbt
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
|empty definition using fallback
non-local guesses:
	 -

Document text:

```scala
val scala3Version = "3.6.3"
val http4sVersion = "0.23.30"
val circeVersion = "0.14.10"
val doobieVersion = "1.0.0-RC7"
val catsEffectVersion = "3.5.3"
val pureConfigVersion = "0.17.5"
val log4catsVersion = "2.6.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "study-game-backend",
    version := "0.1.0",
    scalaVersion := scala3Version,
    
    libraryDependencies ++= Seq(
      // HTTP4s
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      
      // Circe
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-literal" % circeVersion,
      
      // Doobie
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
      
      // Cats Effect
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      
      // Config
      "com.github.pureconfig" %% "pureconfig-core" % pureConfigVersion,
      
      // Logging
      "org.typelevel" %% "log4cats-slf4j" % log4catsVersion,
      "ch.qos.logback" % "logback-classic" % "1.4.14"
    )
  ) 
```

#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.
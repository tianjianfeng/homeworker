package com.homeworker.http.routes

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*
import io.circe.generic.auto.*
import org.http4s.circe.CirceEntityCodec.*
import doobie.util.transactor.Transactor
import scala.concurrent.duration.*
import doobie.implicits.*
import java.io.StringWriter
import io.prometheus.client.exporter.common.TextFormat

class HealthRoutes(xa: Transactor[IO]):
  case class HealthStatus(
    status: String,
    version: String = "0.1.0", // Hardcode version for now
    database: String
  )

  private def checkDatabase: IO[String] =
    sql"SELECT 1".query[Int].unique.transact(xa)
      .timeout(5.seconds)
      .map(_ => "healthy")
      .handleError(_ => "unhealthy")

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "health" =>
      for
        dbStatus <- checkDatabase
        status = HealthStatus(
          status = "up",
          database = dbStatus
        )
        resp <- Ok(status)
      yield resp

    case GET -> Root / "metrics" =>
      val registry = io.prometheus.client.CollectorRegistry.defaultRegistry
      val writer = new StringWriter()
      TextFormat.write004(writer, registry.metricFamilySamples())
      Ok(writer.toString)
  } 
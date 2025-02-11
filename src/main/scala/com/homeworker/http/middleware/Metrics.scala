package com.homeworker.http.middleware

import cats.effect.IO
import org.http4s.*
import io.prometheus.client.*
import scala.concurrent.duration.*

object Metrics:
  private val requestsTotal = Counter.build()
    .name("http_requests_total")
    .help("Total HTTP requests")
    .labelNames("method", "path", "status")
    .register()

  private val requestDuration = Histogram.build()
    .name("http_request_duration_seconds")
    .help("HTTP request duration")
    .labelNames("method", "path")
    .register()

  def middleware(routes: HttpRoutes[IO]): HttpRoutes[IO] =
    HttpRoutes[IO] { req =>
      val startTime = System.nanoTime()
      routes(req).map { resp =>
        val duration = (System.nanoTime() - startTime).nanos.toSeconds
        requestsTotal
          .labels(req.method.name, req.uri.path.renderString, resp.status.code.toString)
          .inc()
        requestDuration
          .labels(req.method.name, req.uri.path.renderString)
          .observe(duration)
        resp
      }
    } 
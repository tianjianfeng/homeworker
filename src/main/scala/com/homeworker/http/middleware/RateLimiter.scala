package com.homeworker.http.middleware

import cats.effect.IO
import org.http4s.*
import scala.collection.concurrent.TrieMap
import java.time.Instant
import scala.concurrent.duration.*
import org.typelevel.ci.*
import cats.data.OptionT
import cats.syntax.all.*
import org.http4s.dsl.io.*
import org.http4s.headers.*

object RateLimiter:
  private case class RateLimit(count: Int, resetAt: Instant)
  private val limits = TrieMap[String, RateLimit]()
  private val MaxRequests = 100
  private val WindowSize = 1.hour

  def middleware(routes: HttpRoutes[IO]): HttpRoutes[IO] =
    HttpRoutes[IO] { req =>
      req.headers.get(ci"X-API-Key").map(_.head.value) match
        case None => routes(req)
        case Some(apiKey) =>
          OptionT.liftF(checkRateLimit(apiKey)).flatMap {
            case true => routes(req)
            case false => 
              OptionT.pure[IO](
                Response[IO](Status.TooManyRequests)
                  .withEntity("Rate limit exceeded. Please try again later.")
              )
          }
    }

  private def checkRateLimit(key: String): IO[Boolean] = IO {
    val now = Instant.now
    limits.get(key) match
      case Some(limit) if limit.resetAt.isAfter(now) =>
        if limit.count < MaxRequests then
          limits.update(key, limit.copy(count = limit.count + 1))
          true
        else false
      case _ =>
        limits.put(key, RateLimit(1, now.plusSeconds(WindowSize.toSeconds)))
        true
  } 
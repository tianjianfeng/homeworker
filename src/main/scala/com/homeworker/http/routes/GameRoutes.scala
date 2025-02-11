package com.homeworker.http.routes

import cats.effect.IO
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.circe.CirceEntityCodec.*
import io.circe.generic.auto.*
import com.homeworker.service.GameService
import com.homeworker.domain.*
import org.slf4j.LoggerFactory

class GameRoutes(gameService: GameService):
  private val logger = LoggerFactory.getLogger(getClass)

  def routes: AuthedRoutes[Long, IO] = AuthedRoutes.of {
    case GET -> Root / "progress" as userId =>
      for
        progress <- gameService.getProgress(userId)
        resp <- Ok(progress)
      yield resp
  } 
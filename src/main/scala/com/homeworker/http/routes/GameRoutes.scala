package com.homeworker.http.routes

import cats.effect.IO
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.circe.CirceEntityCodec.*
import io.circe.generic.auto.*
import com.homeworker.service.GameService
import com.homeworker.domain.*
import org.slf4j.LoggerFactory
import cats.syntax.all.*
import org.http4s.AuthedRoutes

class GameRoutes(gameService: GameService) extends RouteInfo:
  private val logger = LoggerFactory.getLogger(getClass)

  def routes: AuthedRoutes[Long, IO] = 
    logger.info("GameRoutes being initialized...")  // Debug log
    AuthedRoutes.of[Long, IO] {
      case GET -> Root / "progress" as userId =>
        logger.info("Progress route hit!")  // Debug log
        (for
          _ <- IO(logger.info(s"Getting game progress for user: $userId"))
          progress <- gameService.getProgress(userId)
          resp <- Ok(progress)
        yield resp).handleErrorWith { error =>
          logger.error("Error getting game progress:", error)
          InternalServerError(ErrorResponse(error.getMessage))
        }
    }

  override def routeDescription: List[String] = List(
    "├── /progress - Game progress endpoints",
    "│   └── GET / - Get user's game progress and level"
  ) 
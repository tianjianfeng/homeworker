package com.homeworker.http.middleware

import cats.effect.IO
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import com.homeworker.domain.errors.AppError
import com.homeworker.http.ErrorResponse
import io.circe.syntax.*
import cats.data.OptionT
import cats.syntax.all.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.headers.`WWW-Authenticate`
import org.slf4j.LoggerFactory

object ErrorHandler:
  private val logger = LoggerFactory.getLogger(getClass)

  def handle(routes: HttpRoutes[IO]): HttpRoutes[IO] = 
    HttpRoutes[IO] { req =>
      routes(req).handleErrorWith {
        case e: AppError.ValidationError => 
          OptionT.liftF(BadRequest(ErrorResponse(e.message)))
        case e: AppError.NotFoundError => 
          OptionT.liftF(NotFound(ErrorResponse(e.message)))
        case e: AppError.AuthenticationError => 
          val challenge = `WWW-Authenticate`(Challenge("Bearer", ""))
          OptionT.liftF(
            Response[IO](Status.Unauthorized)
              .withEntity(ErrorResponse(e.message))
              .putHeaders(challenge)
              .pure[IO]
          )
        case e: AppError.AuthorizationError => 
          OptionT.liftF(Forbidden(ErrorResponse(e.message)))
        case e: AppError.DatabaseError => 
          logger.error("Database error:", e)
          OptionT.liftF(InternalServerError(ErrorResponse(e.message)))
        case e: Throwable => 
          logger.error("Unexpected error:", e)
          OptionT.liftF(InternalServerError(ErrorResponse("An unexpected error occurred")))
      }
    } 
package com.homeworker.http.routes

import cats.effect.IO
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.circe.CirceEntityCodec.*
import io.circe.generic.auto.*
import com.homeworker.service.UserService
import com.homeworker.domain.*
import org.slf4j.LoggerFactory
import io.circe.{Decoder, Encoder}
import io.circe.syntax.*

class UserRoutes(userService: UserService) extends RouteInfo:
  private val logger = LoggerFactory.getLogger(getClass)

  // Add JSON encoders for UserRole
  given Encoder[UserRole] = Encoder.encodeString.contramap[UserRole](_.toString)
  given Decoder[UserRole] = Decoder.decodeString.emap[UserRole] { str =>
    try Right(UserRole.valueOf(str))
    catch case _: IllegalArgumentException => 
      Left(s"Invalid user role: $str. Valid values are: ${UserRole.values.mkString(", ")}")
  }

  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "users" / "register" =>
      (for
        registerReq <- req.as[RegisterRequest]
        _ = logger.info(s"Registering user: ${registerReq.email}")
        user <- userService.register(registerReq.email, registerReq.password, registerReq.role)
        resp <- Created(user)
      yield resp).handleErrorWith { error =>
        logger.error("Registration error:", error)
        InternalServerError(ErrorResponse(error.getMessage))
      }

    case req @ POST -> Root / "users" / "login" =>
      (for
        loginReq <- req.as[LoginRequest]
        _ = logger.info(s"Login attempt for: ${loginReq.email}")
        authResp <- userService.authenticate(loginReq.email, loginReq.password)
        resp <- Ok(authResp)
      yield resp).handleErrorWith { error =>
        logger.error("Login error:", error)
        InternalServerError(ErrorResponse(error.getMessage))
      }
  }

  def routeDescription: List[String] = List(
    "├── /users",
    "│   ├── POST /register - Register a new user",
    "│   └── POST /login - User login"
  )

  case class RegisterRequest(email: String, password: String, role: UserRole)
  case class LoginRequest(email: String, password: String) 
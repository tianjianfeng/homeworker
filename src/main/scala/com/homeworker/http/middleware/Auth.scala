package com.homeworker.http.middleware

import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware
import com.homeworker.service.UserService
import org.slf4j.LoggerFactory

object Auth:
  private val logger = LoggerFactory.getLogger(getClass)

  def middleware(userService: UserService): AuthMiddleware[IO, Long] =
    val authUser = Kleisli { (request: Request[IO]) =>
      val maybeUserId = for
        auth <- request.headers.get[Authorization]
        token = auth.credentials match
          case Credentials.Token(AuthScheme.Bearer, t) => Some(t)
          case _ => None
        userId <- token.filter(_.startsWith("dummy-token-"))
          .map(_.replace("dummy-token-", ""))
          .flatMap(id => scala.util.Try(id.toLong).toOption)
      yield userId

      OptionT.fromOption[IO](maybeUserId)
    }

    AuthMiddleware(authUser)

  def withAuth(service: AuthedRoutes[Long, IO]): HttpRoutes[IO] =
    middleware(null)(service)  // We don't use userService in the middleware 
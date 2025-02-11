package com.homeworker.http

import io.circe.{Encoder, Json}
import org.http4s.circe.*
import cats.effect.IO
import com.homeworker.domain.User

case class ErrorResponse(message: String)
object ErrorResponse:
  given Encoder[ErrorResponse] = new Encoder[ErrorResponse]:
    final def apply(e: ErrorResponse): Json = Json.obj(
      ("error", Json.fromString(e.message))
    )

case class AuthedRequest[F[_]](user: User, req: org.http4s.Request[F]) 
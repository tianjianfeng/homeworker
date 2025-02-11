package com.homeworker.domain

import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto.*

case class ErrorResponse(error: String)

object ErrorResponse:
  given Encoder[ErrorResponse] = deriveEncoder[ErrorResponse]
  given Decoder[ErrorResponse] = deriveDecoder[ErrorResponse] 
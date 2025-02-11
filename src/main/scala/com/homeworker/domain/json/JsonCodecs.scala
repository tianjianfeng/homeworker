package com.homeworker.domain.json

import io.circe.*
import io.circe.generic.semiauto.*
import io.circe.syntax.*
import com.homeworker.domain.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object JsonCodecs:
  // Custom encoders for LocalDateTime
  given Encoder[LocalDateTime] = new Encoder[LocalDateTime]:
    final def apply(dt: LocalDateTime): Json = Json.fromString(dt.toString)
  
  given Decoder[LocalDateTime] = new Decoder[LocalDateTime]:
    final def apply(c: HCursor): Decoder.Result[LocalDateTime] =
      c.as[String].map(LocalDateTime.parse)

  // Enums
  given Encoder[UserRole] = Encoder.encodeString.contramap[UserRole](_.toString)
  given Decoder[UserRole] = Decoder.decodeString.emap(str => 
    try Right(UserRole.valueOf(str))
    catch case _: IllegalArgumentException => Left(s"Invalid UserRole: $str")
  )

  given Encoder[TaskStatus] = Encoder.encodeString.contramap[TaskStatus](_.toString)
  given Decoder[TaskStatus] = Decoder.decodeString.emap(str =>
    try Right(TaskStatus.valueOf(str))
    catch case _: IllegalArgumentException => Left(s"Invalid TaskStatus: $str")
  )

  // Models
  given Encoder[User] = deriveEncoder[User]
  given Decoder[User] = deriveDecoder[User]

  given Encoder[Task] = deriveEncoder[Task]
  given Decoder[Task] = deriveDecoder[Task]

  given Encoder[GameProgress] = deriveEncoder[GameProgress]
  given Decoder[GameProgress] = deriveDecoder[GameProgress] 
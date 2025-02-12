package com.homeworker.domain

import java.time.LocalDateTime
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto.*

case class GameProgress(
  id: Option[Long],
  userId: Long,
  totalPoints: Int,
  level: Int,
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
)

object GameProgress:
  given Encoder[GameProgress] = deriveEncoder[GameProgress]
  given Decoder[GameProgress] = deriveDecoder[GameProgress] 
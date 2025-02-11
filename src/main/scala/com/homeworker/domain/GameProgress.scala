package com.homeworker.domain

import java.time.LocalDateTime

case class GameProgress(
  id: Option[Long],
  userId: Long,
  level: Int,
  totalPoints: Int,
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
) extends Model 
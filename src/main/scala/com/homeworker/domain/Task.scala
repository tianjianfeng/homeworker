package com.homeworker.domain

import java.time.LocalDateTime
import io.circe.{Encoder, Decoder}

enum TaskStatus:
  case Todo, InProgress, Done

object TaskStatus:
  given Encoder[TaskStatus] = Encoder.encodeString.contramap[TaskStatus](_.toString)
  given Decoder[TaskStatus] = Decoder.decodeString.emap { str =>
    TaskStatus.values.find(_.toString == str)
      .toRight(s"Invalid task status: $str. Valid values are: ${TaskStatus.values.mkString(", ")}")
  }

case class Task(
  id: Option[Long],
  title: String,
  description: String,
  category: String,
  points: Int,
  userId: Long,
  dueDate: LocalDateTime,
  status: TaskStatus = TaskStatus.Todo,
  createdAt: LocalDateTime = LocalDateTime.now,
  updatedAt: LocalDateTime = LocalDateTime.now
) extends Model 
package com.homeworker.service

import cats.effect.IO
import cats.implicits.*

trait Service:
  protected def validateField(field: String, value: String): IO[Unit] =
    if value.trim.isEmpty then
      IO.raiseError(new IllegalArgumentException(s"$field cannot be empty"))
    else IO.unit 
package com.homeworker.domain

import doobie.{Meta, Read, Write}
import doobie.postgres.implicits.*
import doobie.util.meta.Meta.*
import java.time.LocalDateTime
import doobie.postgres.sqlstate

object DoobieMeta:
  // Enum Meta instances
  given Meta[UserRole] = 
    Meta[String].imap(UserRole.valueOf)(_.toString)

  given Meta[TaskStatus] = 
    Meta[String].imap(TaskStatus.valueOf)(_.toString)

  // LocalDateTime Meta instance is already provided by doobie.postgres.implicits.* 
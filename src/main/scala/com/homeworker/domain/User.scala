package com.homeworker.domain

import java.time.LocalDateTime

enum UserRole:
  case Student, Parent, Teacher

case class User(
  id: Option[Long],
  email: String,
  passwordHash: String,
  role: UserRole,
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
) extends Model 
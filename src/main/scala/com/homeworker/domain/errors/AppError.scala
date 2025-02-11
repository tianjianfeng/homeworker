package com.homeworker.domain.errors

sealed trait AppError extends Exception:
  def message: String
  override def getMessage: String = message

object AppError:
  case class ValidationError(message: String) extends AppError
  case class NotFoundError(message: String) extends AppError
  case class AuthenticationError(message: String) extends AppError
  case class AuthorizationError(message: String) extends AppError
  case class DatabaseError(message: String) extends AppError
  case class UnexpectedError(message: String) extends AppError 
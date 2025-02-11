package com.homeworker.validation

import cats.data.ValidatedNel
import cats.syntax.all.*
import cats.effect.IO
import com.homeworker.domain.errors.AppError.ValidationError

object Validator:
  type ValidationResult[A] = ValidatedNel[String, A]
  
  def validate[A](value: A)(rules: (A => ValidationResult[A])*): IO[A] =
    rules
      .foldLeft[ValidationResult[A]](value.validNel)((acc, rule) => 
        acc.productR(rule(value))
      )
      .fold(
        errors => IO.raiseError(ValidationError(errors.toList.mkString(", "))),
        IO.pure
      )

  // Common validation rules
  def nonEmpty(fieldName: String)(str: String): ValidationResult[String] =
    if str.trim.isEmpty then s"$fieldName cannot be empty".invalidNel
    else str.validNel

  def email(str: String): ValidationResult[String] =
    if str.matches("""^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""") then str.validNel
    else "Invalid email format".invalidNel

  def minLength(fieldName: String, min: Int)(str: String): ValidationResult[String] =
    if str.length >= min then str.validNel
    else s"$fieldName must be at least $min characters long".invalidNel

  def positive(fieldName: String)(n: Int): ValidationResult[Int] =
    if n > 0 then n.validNel
    else s"$fieldName must be positive".invalidNel 
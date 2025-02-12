package com.homeworker.service

import cats.effect.IO
import cats.implicits.*
import com.homeworker.domain.*
import com.homeworker.repository.{UserRepository, GameProgressRepository}
import org.slf4j.LoggerFactory
import java.security.MessageDigest
import java.util.Base64
import com.homeworker.domain.errors.AppError.*
import java.time.LocalDateTime

class UserService(
  userRepository: UserRepository,
  gameProgressRepository: GameProgressRepository
) extends Service:
  private val logger = LoggerFactory.getLogger(getClass)

  def findByEmail(email: String): IO[Option[User]] =
    userRepository.findByEmail(email)

  def register(email: String, password: String, role: UserRole): IO[User] =
    for
      _ <- validateField("email", email)
      _ <- validateField("password", password)
      _ <- IO(logger.info(s"Registering new user with email: $email and role: $role"))
      existingUser <- userRepository.findByEmail(email)
      _ <- existingUser match
        case Some(_) => 
          logger.error(s"User with email $email already exists")
          IO.raiseError(new IllegalArgumentException(s"User with email $email already exists"))
        case None => IO.unit
      passwordHash = hashPassword(password)
      user = ModelFactory.newUser(email, passwordHash, role)
      _ <- IO(logger.info(s"Created user object: $user"))
      savedUser <- userRepository.create(user).onError { case e =>
        logger.error(s"Error creating user: ${e.getMessage}", e)
        IO.unit
      }
      _ <- gameProgressRepository.create(
        GameProgress(
          id = None,
          userId = savedUser.id.get,
          totalPoints = 0,
          level = 1,
          createdAt = LocalDateTime.now,
          updatedAt = LocalDateTime.now
        )
      ).onError { case e =>
        logger.error(s"Failed to create game progress for user ${savedUser.id.get}", e)
        IO.unit
      }
      _ <- IO(logger.info(s"Successfully registered user: ${savedUser.email}"))
    yield savedUser

  def authenticate(email: String, password: String): IO[AuthResponse] =
    for
      _ <- IO(logger.info(s"Attempting authentication for email: $email"))
      user <- userRepository.findByEmail(email)
      _ <- user match
        case Some(u) if u.passwordHash == hashPassword(password) => IO.unit
        case _ => 
          logger.error(s"Authentication failed for email: $email")
          IO.raiseError(new IllegalArgumentException("Invalid email or password"))
      token = generateToken(user.get)
      _ <- IO(logger.info(s"Authentication successful for email: $email"))
    yield AuthResponse(token, user.get)

  private def validateEmail(email: String): IO[Unit] =
    if !email.matches(".+@.+\\..+") then
      IO.raiseError(new IllegalArgumentException("Invalid email format"))
    else IO.unit

  private def validateUserDoesNotExist(existingUser: Option[User]): IO[Unit] =
    existingUser match
      case Some(_) => IO.raiseError(ValidationError("Email already exists"))
      case None => IO.unit

  private def validatePassword(password: String): IO[Unit] =
    if password.length < 8 then
      IO.raiseError(new IllegalArgumentException("Password must be at least 8 characters long"))
    else IO.unit

  private def hashPassword(password: String): String =
    // TODO: Implement proper password hashing
    password + "-hashed"

  private def generateToken(user: User): String =
    // TODO: Implement proper JWT token generation
    s"dummy-token-${user.id.get}"

  override def validateField(fieldName: String, value: String): IO[Unit] =
    if value.isEmpty then
      logger.error(s"Validation failed: $fieldName must not be empty")
      IO.raiseError(new IllegalArgumentException(s"$fieldName must not be empty"))
    else IO.unit 
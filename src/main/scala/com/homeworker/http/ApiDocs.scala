package com.homeworker.http

import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import com.homeworker.domain.*
import com.homeworker.domain.json.JsonCodecs.given
import org.http4s.HttpRoutes
import cats.effect.IO
import sttp.tapir.server.http4s.Http4sServerInterpreter
import com.homeworker.http.routes.{UserRoutes, TaskRoutes}
import com.homeworker.service.{GameService, LevelProgress}
import io.circe.generic.auto.*

object ApiDocs:
  private val baseEndpoint = endpoint.in("api")

  case class CreateUserRequest(email: String, password: String, role: UserRole)
  case class LoginRequest(email: String, password: String)
  case class CreateTaskRequest(
    title: String,
    description: String,
    category: String,
    points: Int,
    dueDate: java.time.LocalDateTime
  )

  // User endpoints
  private val userEndpoints = List(
    baseEndpoint.post
      .in("users" / "register")
      .in(jsonBody[CreateUserRequest])
      .out(jsonBody[User])
      .description("Register a new user")
      .tag("Users"),

    baseEndpoint.post
      .in("users" / "login")
      .in(jsonBody[LoginRequest])
      .out(jsonBody[User])
      .description("Authenticate user")
      .tag("Users")
  )

  // Task endpoints
  private val taskEndpoints = List(
    baseEndpoint.post
      .in("tasks")
      .in(jsonBody[CreateTaskRequest])
      .out(jsonBody[Task])
      .description("Create a new task")
      .tag("Tasks"),

    baseEndpoint.get
      .in("tasks")
      .out(jsonBody[List[Task]])
      .description("Get all tasks for the authenticated user")
      .tag("Tasks")
  )

  // Game endpoints
  private val gameEndpoints = List(
    baseEndpoint.get
      .in("progress")
      .out(jsonBody[LevelProgress])
      .description("Get user's game progress")
      .tag("Game")
  )

  private val allEndpoints = userEndpoints ++ taskEndpoints ++ gameEndpoints

  private val swaggerEndpoints = SwaggerInterpreter()
    .fromEndpoints[IO](
      allEndpoints,
      "Study Game API",
      "1.0.0"
    )

  val routes: HttpRoutes[IO] = Http4sServerInterpreter[IO]()
    .toRoutes(swaggerEndpoints) 
package com.homeworker.http.routes

import cats.effect.IO
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.circe.CirceEntityCodec.*
import io.circe.generic.auto.*
import com.homeworker.service.TaskService
import com.homeworker.domain.*
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.http4s.server.AuthMiddleware
import io.circe.{Encoder, Decoder}
import io.circe.syntax.*

class TaskRoutes(taskService: TaskService) extends RouteInfo:
  private val logger = LoggerFactory.getLogger(getClass)

  // Add JSON encoders/decoders for LocalDateTime with ISO format
  given Encoder[LocalDateTime] = Encoder.encodeString.contramap[LocalDateTime](_.toString)
  given Decoder[LocalDateTime] = Decoder.decodeString.emapTry { str =>
    scala.util.Try {
      LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME)
    }
  }

  def routes: AuthedRoutes[Long, IO] = AuthedRoutes.of[Long, IO] {
    case authReq @ POST -> Root / "tasks" as userId =>
      (for
        createReq <- authReq.req.as[CreateTaskRequest]
        _ = logger.info(s"Creating task for user $userId: ${createReq.title}")
        task <- taskService.createTask(
          createReq.title,
          createReq.description,
          createReq.category,
          createReq.points,
          userId,
          createReq.dueDate
        )
        resp <- Created(task)
      yield resp).handleErrorWith { error =>
        logger.error("Error creating task:", error)
        InternalServerError(ErrorResponse(error.getMessage))
      }

    case GET -> Root / "tasks" as userId =>
      (for
        tasks <- taskService.getUserTasks(userId)
        resp <- Ok(tasks)
      yield resp).handleErrorWith { error =>
        logger.error("Error getting tasks:", error)
        InternalServerError(ErrorResponse(error.getMessage))
      }

    case authReq @ PATCH -> Root / "tasks" / LongVar(taskId) / "status" as userId =>
      (for
        updateReq <- authReq.req.as[UpdateTaskStatusRequest]
        _ = logger.info(s"Updating task $taskId status to ${updateReq.status}")
        task <- taskService.updateTaskStatus(taskId, userId, updateReq.status)
        resp <- Ok(task)
      yield resp).handleErrorWith { error =>
        logger.error("Error updating task status:", error)
        InternalServerError(ErrorResponse(error.getMessage))
      }
  }

  override def routeDescription: List[String] = List(
    "├── /tasks",
    "│   ├── GET / - List all tasks",
    "│   ├── POST / - Create a new task",
    "│   └── PATCH /{id}/status - Update task status"
  )

  final case class CreateTaskRequest(
    title: String,
    description: String,
    category: String,
    points: Int,
    dueDate: LocalDateTime
  )

  final case class UpdateTaskStatusRequest(
    status: TaskStatus
  ) 
package com.homeworker.service

import cats.effect.IO
import cats.implicits.*
import com.homeworker.domain.*
import com.homeworker.repository.{TaskRepository, GameProgressRepository}
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class TaskService(
  taskRepo: TaskRepository,
  gameProgressRepo: GameProgressRepository
):
  private val logger = LoggerFactory.getLogger(getClass)

  def createTask(
    title: String,
    description: String,
    category: String,
    points: Int,
    userId: Long,
    dueDate: LocalDateTime
  ): IO[Task] =
    logger.info(s"Creating task: $title for user: $userId")
    val task = Task(
      id = None,
      title = title,
      description = description,
      category = category,
      points = points,
      userId = userId,
      dueDate = dueDate,
      status = TaskStatus.Todo
    )
    taskRepo.create(task)

  def getUserTasks(userId: Long): IO[List[Task]] =
    taskRepo.findByUserId(userId)

  def updateTaskStatus(taskId: Long, status: TaskStatus): IO[Task] =
    for
      task <- taskRepo.updateStatus(taskId, status)
      _ <- status match
        case TaskStatus.Done => gameProgressRepo.updateProgress(task.userId, task.points)
        case _ => IO.unit
    yield task

  def validateField(fieldName: String, value: String): IO[Unit] =
    if value.isEmpty then
      logger.error(s"Validation failed: $fieldName must not be empty")
      IO.raiseError(new IllegalArgumentException(s"$fieldName must not be empty"))
    else IO.unit

  private def validatePoints(points: Int): IO[Unit] =
    if points <= 0 then
      IO.raiseError(new IllegalArgumentException("Points must be positive"))
    else IO.unit 
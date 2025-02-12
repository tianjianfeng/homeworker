package com.homeworker.repository

import cats.effect.IO
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import com.homeworker.domain.*
import com.homeworker.domain.DoobieMeta.given
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class TaskRepository(val xa: Transactor[IO]) extends Repository[IO, Task]:
  private val logger = LoggerFactory.getLogger(getClass)

  def query[B](q: Query0[B]): IO[List[B]] = q.stream.compile.toList.transact(xa)
  def update(u: Update0): IO[Int] = u.run.transact(xa)

  def create(task: Task): IO[Task] =
    sql"""
      INSERT INTO tasks (title, description, category, points, user_id, due_date, status)
      VALUES (${task.title}, ${task.description}, ${task.category}, ${task.points}, 
              ${task.userId}, ${task.dueDate}, ${task.status})
      RETURNING id, title, description, category, points, user_id, due_date, status, created_at, updated_at
    """.query[Task].unique.transact(xa)

  def findById(id: Long): IO[Option[Task]] =
    sql"""
      SELECT id, title, description, category, points, user_id, due_date, status, created_at, updated_at
      FROM tasks
      WHERE id = $id
    """.query[Task].option.transact(xa)

  def findByUserId(userId: Long): IO[List[Task]] =
    sql"""
      SELECT id, title, description, category, points, user_id, due_date, status, created_at, updated_at
      FROM tasks
      WHERE user_id = $userId
      ORDER BY due_date ASC
    """.query[Task].to[List].transact(xa)

  def updateStatus(taskId: Long, status: TaskStatus): IO[Task] =
    sql"""
      UPDATE tasks 
      SET status = $status, updated_at = CURRENT_TIMESTAMP
      WHERE id = $taskId
      RETURNING id, title, description, category, points, user_id, due_date, status, created_at, updated_at
    """.query[Task].unique.transact(xa)

  def delete(id: Long): IO[Int] =
    sql"DELETE FROM tasks WHERE id = $id".update.run.transact(xa)

  def update(task: Task): IO[Task] =
    sql"""
      UPDATE tasks
      SET title = ${task.title},
          description = ${task.description},
          category = ${task.category},
          points = ${task.points},
          due_date = ${task.dueDate},
          status = ${task.status},
          updated_at = ${task.updatedAt}
      WHERE id = ${task.id}
      RETURNING id, title, description, category, points, user_id, due_date, status, created_at, updated_at
    """.query[Task].unique.transact(xa) 
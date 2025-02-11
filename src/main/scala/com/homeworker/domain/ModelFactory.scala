package com.homeworker.domain

import java.time.LocalDateTime

object ModelFactory:
  def newUser(email: String, passwordHash: String, role: UserRole): User =
    User(
      id = None,
      email = email,
      passwordHash = passwordHash,
      role = role,
      createdAt = LocalDateTime.now,
      updatedAt = LocalDateTime.now
    )

  def newTask(
    title: String,
    description: String,
    category: String,
    points: Int,
    userId: Long,
    dueDate: LocalDateTime
  ): Task =
    val now = LocalDateTime.now
    Task(
      id = None,
      title = title,
      description = description,
      category = category,
      points = points,
      userId = userId,
      dueDate = dueDate,
      status = TaskStatus.Todo,
      createdAt = now,
      updatedAt = now
    )

  def newGameProgress(userId: Long): GameProgress =
    val now = LocalDateTime.now
    GameProgress(None, userId, 0, 1, now, now) 
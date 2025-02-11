package com.homeworker.service

import cats.effect.IO
import cats.implicits.*
import com.homeworker.domain.*
import com.homeworker.repository.{GameProgressRepository, TaskRepository}
import org.slf4j.LoggerFactory

class GameService(
  gameProgressRepo: GameProgressRepository,
  taskRepo: TaskRepository
):
  private val logger = LoggerFactory.getLogger(getClass)

  def getProgress(userId: Long): IO[GameProgress] =
    for
      progressOpt <- gameProgressRepo.findByUserId(userId)
      progress <- progressOpt match
        case Some(p) => IO.pure(p)
        case None => IO.raiseError(new IllegalStateException(s"No game progress found for user $userId"))
    yield progress

  def getUserProgress(userId: Long): IO[GameProgress] =
    gameProgressRepo.getOrCreate(userId)

  def calculateLevelProgress(progress: GameProgress): IO[LevelProgress] =
    val pointsPerLevel = 100
    val currentLevelPoints = progress.totalPoints % pointsPerLevel
    val percentage = (currentLevelPoints.toDouble / pointsPerLevel) * 100
    IO.pure(LevelProgress(
      currentLevel = progress.level,
      currentPoints = progress.totalPoints,
      nextLevelPoints = (progress.level * pointsPerLevel),
      progressPercentage = percentage.toInt
    ))

case class LevelProgress(
  currentLevel: Int,
  currentPoints: Int,
  nextLevelPoints: Int,
  progressPercentage: Int
) 
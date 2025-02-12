package com.homeworker.service

import cats.effect.IO
import cats.implicits.*
import com.homeworker.domain.*
import com.homeworker.repository.{GameProgressRepository, TaskRepository}
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

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
        case None => 
          logger.info(s"Creating new game progress for user $userId")
          val newProgress = GameProgress(
            id = None,
            userId = userId,
            totalPoints = 0,
            level = 1,
            createdAt = LocalDateTime.now,
            updatedAt = LocalDateTime.now
          )
          gameProgressRepo.create(newProgress)
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
package com.homeworker.repository

import cats.effect.IO
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import com.homeworker.domain.*
import com.homeworker.domain.DoobieMeta.given
import org.slf4j.LoggerFactory

class GameProgressRepository(val xa: Transactor[IO]) extends Repository[IO, GameProgress]:
  private val logger = LoggerFactory.getLogger(getClass)

  def query[B](q: Query0[B]): IO[List[B]] = q.stream.compile.toList.transact(xa)
  def update(u: Update0): IO[Int] = u.run.transact(xa)

  def getOrCreate(userId: Long): IO[GameProgress] =
    sql"""
      INSERT INTO game_progress (user_id, level, total_points, created_at, updated_at)
      VALUES ($userId, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
      ON CONFLICT (user_id) DO UPDATE SET updated_at = CURRENT_TIMESTAMP
      RETURNING id, user_id, level, total_points, created_at, updated_at
    """.query[GameProgress].unique.transact(xa)

  def updateProgress(userId: Long, points: Int): IO[GameProgress] =
    sql"""
      UPDATE game_progress
      SET total_points = total_points + $points,
          level = 1 + (total_points + $points) / 100,
          updated_at = CURRENT_TIMESTAMP
      WHERE user_id = $userId
      RETURNING id, user_id, level, total_points, created_at, updated_at
    """.query[GameProgress].unique.transact(xa)

  def create(userId: Long): IO[GameProgress] =
    sql"""
      INSERT INTO game_progress (user_id, level, total_points)
      VALUES ($userId, 1, 0)
      RETURNING id, user_id, level, total_points, created_at, updated_at
    """.query[GameProgress].unique.transact(xa)

  def update(progress: GameProgress): IO[GameProgress] =
    sql"""
      UPDATE game_progress
      SET level = ${progress.level},
          total_points = ${progress.totalPoints},
          updated_at = CURRENT_TIMESTAMP
      WHERE user_id = ${progress.userId}
      RETURNING id, user_id, level, total_points, created_at, updated_at
    """.query[GameProgress].unique.transact(xa)

  def findByUserId(userId: Long): IO[Option[GameProgress]] =
    sql"""
      SELECT id, user_id, level, total_points, created_at, updated_at
      FROM game_progress
      WHERE user_id = $userId
    """.query[GameProgress].option.transact(xa) 
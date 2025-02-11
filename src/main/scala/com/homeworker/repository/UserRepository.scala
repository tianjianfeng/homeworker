package com.homeworker.repository

import cats.effect.IO
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import com.homeworker.domain.*
import com.homeworker.domain.DoobieMeta.given

class UserRepository(val xa: Transactor[IO]) extends Repository[IO, User]:
  def query[B](q: Query0[B]): IO[List[B]] = q.stream.compile.toList.transact(xa)
  def update(u: Update0): IO[Int] = u.run.transact(xa)

  def create(user: User): IO[User] =
    sql"""
      INSERT INTO users (email, password_hash, role, created_at, updated_at)
      VALUES (${user.email}, ${user.passwordHash}, ${user.role}, 
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
      RETURNING id, email, password_hash, role, created_at, updated_at
    """.query[User].unique.transact(xa)

  def findById(id: Long): IO[Option[User]] =
    sql"""
      SELECT id, email, password_hash, role, created_at, updated_at
      FROM users WHERE id = $id
    """.query[User].option.transact(xa)

  def findByEmail(email: String): IO[Option[User]] =
    sql"""
      SELECT id, email, password_hash, role, created_at, updated_at
      FROM users
      WHERE email = $email
    """.query[User].option.transact(xa) 
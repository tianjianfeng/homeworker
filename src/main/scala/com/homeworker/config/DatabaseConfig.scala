package com.homeworker.config

import cats.effect.{IO, Resource}
import doobie.util.transactor.Transactor
import doobie.hikari.HikariTransactor
import com.zaxxer.hikari.HikariConfig
import scala.concurrent.ExecutionContext
import pureconfig.*
import pureconfig.generic.derivation.default.*
import org.flywaydb.core.Flyway

case class DatabaseConfig(
    url: String,
    user: String,
    password: String,
    driver: String = "org.postgresql.Driver"
) derives ConfigReader

object DatabaseConfig:
  def transactor(config: DatabaseConfig): Resource[IO, Transactor[IO]] =
    for
      ec <- Resource.pure[IO, ExecutionContext](ExecutionContext.global)
      xa <- HikariTransactor.newHikariTransactor[IO](
        config.driver,
        config.url,
        config.user,
        config.password,
        ec
      )
    yield xa

  def initializeDb(config: DatabaseConfig): IO[Unit] = IO {
    val flyway = Flyway.configure()
      .dataSource(config.url, config.user, config.password)
      .load()
    flyway.migrate()
  } 
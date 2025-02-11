package com.homeworker.config

import pureconfig.*
import pureconfig.generic.derivation.default.*
import cats.effect.IO

case class AppConfig(
  server: ServerConfig,
  database: DatabaseConfig,
  auth: AuthConfig
) derives ConfigReader

case class ServerConfig(
  host: String,
  port: Int
) derives ConfigReader

case class AuthConfig(
  secretKey: String,
  tokenExpiration: Long // in minutes
) derives ConfigReader

object AppConfig:
  def load: IO[AppConfig] = IO {
    ConfigSource.default.load[AppConfig] match
      case Left(errors) => 
        throw new RuntimeException(s"Failed to load configuration: ${errors.prettyPrint()}")
      case Right(config) => config
  } 
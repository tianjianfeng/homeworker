package com.homeworker

import cats.effect.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.server.Router
import com.comcast.ip4s.*
import com.homeworker.config.{AppConfig, DatabaseConfig}
import com.homeworker.repository.*
import com.homeworker.service.*
import com.homeworker.http.routes.*
import com.homeworker.http.middleware.{Auth, ErrorHandler, Metrics, RateLimiter}
import com.homeworker.http.ApiDocs
import org.http4s.implicits.*
import cats.syntax.all.*
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory

object Main extends IOApp:
  private val logger = LoggerFactory.getLogger(getClass)

  private def logRoutes(prefix: String = "", depth: Int = 0): Unit =
    val indent = "│   " * depth
    val branch = if depth == 0 then "" else if prefix.contains("/") then "├── " else "└── "
    logger.info(s"$indent$branch$prefix")

  def run(args: List[String]): IO[ExitCode] =
    for
      _ <- IO.println("Starting Homeworker Backend...")
      config <- AppConfig.load
      _ <- IO.println(s"Loaded configuration: $config")
      _ <- runMigrations(config)
      xa <- DatabaseConfig.transactor(config.database).use { xa =>
        // Repositories
        val userRepo = new UserRepository(xa)
        val taskRepo = new TaskRepository(xa)
        val gameProgressRepo = new GameProgressRepository(xa)

        // Services
        val userService = new UserService(userRepo, gameProgressRepo)
        val taskService = new TaskService(taskRepo, gameProgressRepo)
        val gameService = new GameService(gameProgressRepo, taskRepo)

        // Auth middleware
        val authMiddleware = Auth.middleware(userService)

        // Routes
        val userRoutes = new UserRoutes(userService)
        val taskRoutes = new TaskRoutes(taskService)
        val gameRoutes = new GameRoutes(gameService)
        val healthRoutes = new HealthRoutes(xa)

        // Debug print to verify routes are being combined
        logger.info("Initializing routes...")
        logger.info("Game routes initialized")

        // Combine routes - FIXED ORDER
        val apiRoutes = ErrorHandler.handle(
          userRoutes.routes <+>
          authMiddleware(gameRoutes.routes <+> taskRoutes.routes)
        )

        val httpApp = Router(
          "/api" -> RateLimiter.middleware(Metrics.middleware(apiRoutes)),
          "/docs" -> ApiDocs.routes,
          "/" -> healthRoutes.routes
        ).orNotFound

        val finalHttpApp = Logger.httpApp(true, true)(httpApp)

        logger.info("Starting HTTP server...")
        EmberServerBuilder
          .default[IO]
          .withHost(Host.fromString(config.server.host).get)
          .withPort(Port.fromInt(config.server.port).get)
          .withHttpApp(finalHttpApp)
          .build
          .use { server =>
            logger.info(s"Server started at ${config.server.host}:${config.server.port}")
            IO.never
          }
          .as(ExitCode.Success)
      }
    yield xa

  private def runMigrations(config: AppConfig): IO[Unit] = IO {
    logger.info("Running database migrations...")
    val flyway = Flyway.configure()
      .dataSource(
        config.database.url,
        config.database.user,
        config.database.password
      )
      .load()
    val result = flyway.migrate()
    logger.info(s"Applied ${result.migrationsExecuted} migrations")
  }.handleErrorWith { error =>
    logger.error("Migration failed:", error)
    IO.raiseError(error)
  } 
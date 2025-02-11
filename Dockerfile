# Build stage
FROM sbtscala/scala-sbt:graalvm-community-22.0.1_1.10.7_3.6.3 AS build

WORKDIR /app
COPY . .
RUN sbt clean assembly

# Run stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
COPY --from=build /app/target/scala-3.6.3/homeworker.jar app.jar

EXPOSE 8080

ENV APP_ENV=prod

ENTRYPOINT ["java", "-jar", "app.jar"] 
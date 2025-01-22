# Этап сборки
FROM eclipse-temurin:17-jdk-alpine AS build

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

COPY build.gradle.kts .
COPY gradle/wrapper/ gradle/wrapper/
COPY src src
COPY --chmod=755 gradlew .

RUN ./gradlew build -x test

# Этап запуска
FROM eclipse-temurin:17-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
# ── Stage 1: Build ──────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy Gradle wrapper và config trước để cache dependencies
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Cấp quyền execute cho gradlew
RUN chmod +x gradlew

# Download dependencies (tách layer riêng để cache)
RUN ./gradlew dependencies --no-daemon 2>/dev/null || true

# Copy source và build JAR (bỏ qua tests)
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# ── Stage 2: Runtime ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Tạo user non-root
RUN addgroup -S metahrm && adduser -S metahrm -G metahrm

# Copy JAR từ build stage
# Gradle output: build/libs/*.jar
COPY --from=build /app/build/libs/*.jar app.jar

RUN chown metahrm:metahrm app.jar
USER metahrm

# Railway inject PORT tự động
EXPOSE 8080

# Chạy với profile prod
ENTRYPOINT ["java", \
  "-Dspring.profiles.active=prod", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]

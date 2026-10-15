# One image recipe for every backend deployable (monolith, services, platform).
#   docker build -f backend/Dockerfile --build-arg MODULE=monolith backend
# MODULE is the module's path inside backend/ (e.g. monolith, platform/api-gateway, services/notification-service).

FROM maven:3.9.11-eclipse-temurin-21-alpine AS build
ARG MODULE
WORKDIR /src
COPY . .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -pl "${MODULE}" -am package -DskipTests \
 && cp "${MODULE}"/target/*.jar /app.jar

FROM eclipse-temurin:21-jre-alpine
ARG PORT=8080
RUN apk add --no-cache curl && addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=build /app.jar app.jar
RUN mkdir -p /app/uploads && chown -R appuser:appgroup /app
USER appuser
ENV SERVER_PORT=${PORT}
EXPOSE ${PORT}
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -fs "http://localhost:${SERVER_PORT}/actuator/health" || exit 1
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

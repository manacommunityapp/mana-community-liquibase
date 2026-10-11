FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
COPY src ./src
RUN ./mvnw -ntp package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime
LABEL maintainer="mana-community-dev"
LABEL version="1.0"
RUN addgroup -S manadb && adduser -S manadb -G manadb
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
USER manadb
EXPOSE 8090
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -q --spider http://localhost:8090/actuator/health || exit 1
ENTRYPOINT ["java","-jar","app.jar"]

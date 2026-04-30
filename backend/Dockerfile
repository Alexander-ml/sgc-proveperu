# ── Etapa 1: Build ──────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copiar pom.xml primero para cachear dependencias Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn package -DskipTests -B

# ── Etapa 2: Runtime ────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Dspring.profiles.active=dev", \
  "-Duser.timezone=America/Lima", \
  "-jar", "app.jar"]
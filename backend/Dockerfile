FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# copy project files from computer
COPY pom.xml .
COPY src ./src

# build jar files
# skipped Test
RUN mvn clean package -DskipTests

# switch to jre image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# copy the final jar file from builder stage
# help keep final image smaller
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
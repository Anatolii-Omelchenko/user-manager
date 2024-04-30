FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean install -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

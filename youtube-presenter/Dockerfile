FROM openjdk:8-jdk-alpine

WORKDIR /app

RUN mkdir audio

# Java application configuration
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw install -DskipTests

COPY ./target/youtube-presenter-1.0.0.jar ./youtube-presenter.jar

ENTRYPOINT [ "java", "-jar", "/app/youtube-presenter.jar"]
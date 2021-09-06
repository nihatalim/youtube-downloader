FROM openjdk:8-jdk-alpine

WORKDIR /app

# Java application configuration
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw install -DskipTests

COPY ./target/youtube-converter-1.0.0.jar ./youtube-converter.jar

RUN apk add ffmpeg

ENTRYPOINT [ "java", "-jar", "/app/youtube-converter.jar"]
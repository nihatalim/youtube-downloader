FROM openjdk:8-jdk-alpine

WORKDIR /app

# Java application configuration
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw install -DskipTests

COPY ./target/youtube-downloader-1.0.0.jar ./youtube-downloader.jar

# Adding python
RUN apk add python

# Configuration of the youtube-dl
RUN wget https://yt-dl.org/downloads/latest/youtube-dl
RUN mv youtube-dl /usr/bin/youtube-dl
RUN chmod a+rx /usr/bin/youtube-dl

ENTRYPOINT [ "java", "-jar", "/app/youtube-downloader.jar"]
FROM gradle:4.10-jdk8-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon 

FROM openjdk:8-jre-slim

EXPOSE 1024

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/haruna.jar

COPY /config_example/HarunaConfig.json /app/HarunaConfig.json

ENTRYPOINT ["java", "-jar", "/app/haruna.jar"]
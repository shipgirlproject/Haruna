FROM gradle:8.9.0-jdk22-alpine AS build

LABEL stage=build

RUN mkdir -p /root/build

WORKDIR /root/build

COPY . /root/build

RUN gradle build --no-daemon

FROM openjdk:22-slim

WORKDIR /usr/src/app/

COPY --from=build /root/build/build/libs/haruna*.jar ./haruna.jar

ENTRYPOINT ["java", "-jar", "haruna.jar"]
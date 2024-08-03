FROM gradle:8.9.0-jdk22-alpine as build

LABEL stage=build

RUN mkdir -p /root/build

WORKDIR /root/build

COPY . /root/build

RUN gradle build --no-daemon

FROM openjdk:22-slim

RUN groupadd -g 101 haruna && \
    useradd -r -u 101 -g haruna haruna && \
    mkdir -p /home/haruna 

COPY --from=build /root/build/build/libs/haruna*.jar /home/haruna/haruna.jar

RUN chown -R haruna:haruna /home/haruna

USER haruna

WORKDIR /home/haruna

ENTRYPOINT ["java", "-jar", "haruna.jar"]
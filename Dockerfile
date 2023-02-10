FROM gradle:jdk17-focal AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

FROM openjdk:17-jdk-alpine
MAINTAINER Webdroid "webdroid0001@gmail.com"

RUN mkdir /home

COPY --from=build /home /home
#COPY --from=build /home/gradle/src/src/main/resources/maxmind/GeoLite2-Country.mmdb /app/GeoLite2-Country.mmdb

ENTRYPOINT ["java","-jar","/app/src/build/libs/webdroid-authorization-server-0.0.1.jar"]

EXPOSE 9000

#ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/spring-boot-application.jar"]
#RUN apk --update --no-cache add curl
#HEALTHCHECK CMD curl -f http://localhost:8080/health || exit 1
FROM gradle:6.8.3-jdk8 as build
RUN mkdir /cv
COPY --chown=gradle:gradle settings.gradle.kts /cv
COPY --chown=gradle:gradle build.gradle.kts /cv
COPY --chown=gradle:gradle gradle.properties /cv
COPY --chown=gradle:gradle ./backend /cv/backend
COPY --chown=gradle:gradle ./buildSrc /cv/buildSrc
COPY --chown=gradle:gradle ./shared /cv/shared
WORKDIR /cv
RUN gradle :backend:installDist --no-daemon --info

FROM openjdk:8-jre-alpine
EXPOSE 8080:8080
EXPOSE 8090:8090

RUN mkdir /app
COPY --from=build /cv/backend/build/install/backend/ /app/
COPY ./backend/test.jks /app/bin/

WORKDIR /app/bin
CMD ["./backend"]

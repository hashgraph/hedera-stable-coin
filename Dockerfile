FROM openjdk:14 as build

COPY ./gradle /opt/stable-coin/gradle
COPY ./gradlew /opt/stable-coin/gradlew
COPY ./build.gradle /opt/stable-coin/build.gradle
COPY ./settings.gradle /opt/stable-coin/settings.gradle

COPY ./proto /opt/stable-coin/proto
COPY ./sdk /opt/stable-coin/sdk
COPY ./src /opt/stable-coin/src

RUN cd /opt/stable-coin/ && ./gradlew --no-daemon assemble

FROM openjdk:14

RUN groupadd --gid 1000 appuser
RUN useradd --uid 1000 --gid appuser appuser
USER appuser

COPY --from=build /opt/stable-coin/build/libs/stable-coin-0.2.0.jar /opt/stable-coin/stable-coin-0.2.0.jar

WORKDIR /demo

CMD "java" "-jar" /opt/stable-coin/stable-coin-0.2.0.jar
EXPOSE 9000

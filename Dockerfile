FROM openjdk:14

COPY ./build/libs/stable-coin-0.2.0.jar /srv/stable-coin-0.2.0.jar

WORKDIR /srv
CMD java -jar /srv/stable-coin-0.2.0.jar
EXPOSE 9000

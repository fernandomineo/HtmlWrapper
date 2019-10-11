FROM openjdk:8-jdk-alpine
VOLUME /tmp

COPY target/htmlwrapper-0.1-SNAPSHOT.jar htmlwrapper-0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","htmlwrapper-0.1-SNAPSHOT.jar"]
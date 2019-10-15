FROM openjdk:8-jdk-alpine
VOLUME /tmp

COPY target/htmlwrapper-1.0.jar htmlwrapper-1.0.jar
ENTRYPOINT ["java","-jar","htmlwrapper-1.0.jar"]
FROM openjdk:20
VOLUME /tmp
COPY target/Authentication.jar Authentication.jar
ENTRYPOINT ["java","-jar","./Authentication.jar"]
EXPOSE 8027
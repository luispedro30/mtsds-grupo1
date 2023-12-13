FROM openjdk:20
VOLUME /tmp
COPY target/users.jar users.jar
ENTRYPOINT ["java","-jar","./users.jar"]
EXPOSE 8012

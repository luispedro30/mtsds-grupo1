FROM openjdk:20
VOLUME /tmp
COPY target/users.jar users.jar
ENTRYPOINT ["java","-jar","/user.jar"]
EXPOSE 8085

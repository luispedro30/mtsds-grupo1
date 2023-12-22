FROM openjdk:20
VOLUME /tmp
COPY target/Email.jar email.jar
ENTRYPOINT ["java","-jar","./email.jar"]
EXPOSE 8020
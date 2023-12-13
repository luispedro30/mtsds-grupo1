FROM openjdk:20
VOLUME /tmp
COPY target/Reviews.jar reviews.jar
ENTRYPOINT ["java","-jar","./reviews.jar"]
EXPOSE 8010
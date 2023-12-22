FROM openjdk:20
VOLUME /tmp
COPY target/Payment.jar Payment.jar
ENTRYPOINT ["java","-jar","./Payment.jar"]
EXPOSE 8010
FROM openjdk:20
VOLUME /tmp
COPY target/payment.jar payments.jar
ENTRYPOINT ["java","-jar","./payments.jar"]
EXPOSE 8010
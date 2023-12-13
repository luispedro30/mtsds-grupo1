FROM openjdk:20
VOLUME /tmp
COPY target/Shipping.jar shipping.jar
ENTRYPOINT ["java","-jar","./shipping.jar"]
EXPOSE 8010
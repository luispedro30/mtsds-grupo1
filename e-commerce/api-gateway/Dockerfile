FROM openjdk:20
VOLUME /tmp
COPY target/api-gateway.jar api-gateway.jar
ENTRYPOINT ["java","-jar","./api-gateway.jar"]
EXPOSE 8087
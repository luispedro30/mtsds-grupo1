FROM openjdk:20
VOLUME /tmp
COPY target/Orders.jar orders.jar
ENTRYPOINT ["java","-jar","./orders.jar"]
EXPOSE 8004
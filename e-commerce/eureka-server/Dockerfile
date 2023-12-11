FROM openjdk:20
VOLUME /tmp
COPY target/eureka-server.jar eureka-server.jar
ENTRYPOINT ["java","-jar","./eureka-server.jar"]
EXPOSE 8084
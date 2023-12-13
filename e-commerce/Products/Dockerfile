FROM openjdk:20
VOLUME /tmp
COPY target/products.jar products.jar
ENTRYPOINT ["java","-jar","./products.jar"]
EXPOSE 8091
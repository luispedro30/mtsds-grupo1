FROM openjdk:20
VOLUME /tmp
COPY target/Wallet.jar wallet.jar
ENTRYPOINT ["java","-jar","./wallet.jar"]
EXPOSE 7777
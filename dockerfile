FROM openjdk:17-oracle
COPY ./target/order-service-0.0.1-SNAPSHOT.jar order-service.jar
CMD ["java","-jar","order-service.jar"]
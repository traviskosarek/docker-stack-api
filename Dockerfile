FROM openjdk:8-jdk-alpine

WORKDIR /image-working-directory

COPY target/docker-stack-api-0.1.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
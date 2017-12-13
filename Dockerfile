FROM openjdk:8-jdk-alpine

WORKDIR /image-working-directory

COPY target/docker-stack-api-0.1.0.jar app.jar

EXPOSE 8080

RUN set -ex \
 && curl -fL "https://download.docker.com/linux/static/stable/`uname -m`/docker-17.06.0-ce.tgz" -o docker.tgz \
 && tar --extract --file docker.tgz --strip-components 1 --directory /usr/local/bin \
 && rm docker.tgz \
 && docker -

ENTRYPOINT ["java", "-jar", "app.jar"]
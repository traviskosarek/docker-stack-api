FROM openjdk:8

WORKDIR /image-working-directory

COPY target/docker-stack-api-0.1.0.jar app.jar

EXPOSE 8080

ENV DOCKER_CHANNEL stable
ENV DOCKER_VERSION 17.06.0-ce

RUN set -ex \
 && curl -fL "https://download.docker.com/linux/static/${DOCKER_CHANNEL}/`uname -m`/docker-${DOCKER_VERSION}.tgz" -o docker.tgz \
 && tar --extract --file docker.tgz --strip-components 1 --directory /usr/local/bin \
 && rm docker.tgz \
 && docker -v

ENTRYPOINT ["java", "-jar", "app.jar"]
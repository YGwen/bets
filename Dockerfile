FROM maven:3.5.3-jdk-9
WORKDIR .
RUN mvn clean install
COPY src/main/target .

FROM openjdk:9-jre-slim

LABEL maintainer="Michael Staehler"

# VOLUME /tmp

# Add custom user for running the image as a non-root user
RUN useradd -ms /bin/bash fred

RUN set -ex; \
        apt-get update \
        && apt-get install -y less \
        && chown -R fred:0 /home/fred \
        && chmod -R g+rw /home/fred \
        && chmod g+w /etc/passwd

ENV JAVA_OPTS="-Xms256m -Xmx1024m -Duser.timezone=Europe/Berlin"

EXPOSE 8080

COPY COPY --from=0 fredbet.jar /home/fred/fredbet.jar

RUN sh -c 'touch /home/fred/fredbet.jar'

USER fred

WORKDIR /home/fred

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /home/fred/fredbet.jar" ]

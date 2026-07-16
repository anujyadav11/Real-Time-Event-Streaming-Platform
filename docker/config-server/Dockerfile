FROM eclipse-temurin:25-jre

RUN apt-get update \
    && apt-get install --no-install-recommends -y curl \
    && rm -rf /var/lib/apt/lists/*

LABEL maintainer="Anuj Yadav"
LABEL project="Real-Time-Event-Streaming-Platform"
LABEL service="config-server"
LABEL version="1.0.0"
LABEL description="Config Server"

WORKDIR /app

COPY config-server/target/config-server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8888

ENV JAVA_OPTS="-XX:+UseContainerSupport"

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar app.jar"]

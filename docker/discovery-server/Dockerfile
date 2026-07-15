FROM eclipse-temurin:25-jre

RUN apt-get update \
    && apt-get install --no-install-recommends -y curl \
    && rm -rf /var/lib/apt/lists/*

LABEL maintainer="Anuj Yadav"
LABEL project="Real-Time-Event-Streaming-Platform"
LABEL service="discovery-server"

WORKDIR /app

COPY discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8761

ENV JAVA_OPTS=""

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar app.jar"]

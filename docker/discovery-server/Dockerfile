FROM eclipse-temurin:25-jre

RUN apt-get update \
    && apt-get install --no-install-recommends -y curl \
    && rm -rf /var/lib/apt/lists/*

LABEL maintainer="Anuj Yadav"
LABEL project="Real-Time-Event-Streaming-Platform"
LABEL service="discovery-server"
LABEL version="1.0.1"
LABEL description="Discovery server"

WORKDIR /app

COPY discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar app.jar

USER 10001

EXPOSE 8761

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:InitialRAMPercentage=25.0 -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar app.jar"]

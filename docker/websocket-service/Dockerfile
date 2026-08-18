FROM eclipse-temurin:25-jre

LABEL maintainer="Anuj Yadav"
LABEL project="Real-Time-Event-Streaming-Platform"
LABEL service="websocket-service"
LABEL version="1.0.1"
LABEL description="Websocket Service"

WORKDIR /app
COPY websocket-service/target/websocket-service-0.0.1-SNAPSHOT.jar app.jar

USER 10001

EXPOSE 8088

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:InitialRAMPercentage=25.0 -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

FROM eclipse-temurin:25-jre

LABEL maintainer="Anuj Yadav"
LABEL project="Real-Time-Event-Streaming-Platform"
LABEL service="websocket-service"
LABEL version="1.0.0"
LABEL description="Websocket Service"

WORKDIR /app
COPY websocket-service/target/websocket-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8088
ENV JAVA_OPTS="-XX:+UseContainerSupport"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

FROM eclipse-temurin:25-jre

WORKDIR /app
COPY websocket-service/target/websocket-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8088
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

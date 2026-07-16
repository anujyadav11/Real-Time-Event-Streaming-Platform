FROM eclipse-temurin:25-jre

LABEL maintainer="Anuj Yadav"
LABEL project="Real-Time-Event-Streaming-Platform"
LABEL service="payment-service"
LABEL version="1.0.0"
LABEL description="Payment Service"

WORKDIR /app
COPY payment-service/target/payment-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8083
ENV JAVA_OPTS="-XX:+UseContainerSupport"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

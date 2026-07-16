FROM eclipse-temurin:25-jre

LABEL maintainer="Anuj Yadav"
LABEL project="Real-Time-Event-Streaming-Platform"
LABEL service="order-service"
LABEL version="1.0.0"
LABEL description="Order Service"

WORKDIR /app

COPY order-service/target/order-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENV JAVA_OPTS="-XX:+UseContainerSupport"

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar app.jar"]

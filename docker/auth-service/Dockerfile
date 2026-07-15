FROM eclipse-temurin:25-jre

LABEL maintainer="Anuj Yadav"
LABEL project="Real-Time-Event-Streaming-Platform"
LABEL service="auth-service"

WORKDIR /app

COPY auth-service/target/auth-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8087

ENV JAVA_OPTS=""

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar app.jar"]

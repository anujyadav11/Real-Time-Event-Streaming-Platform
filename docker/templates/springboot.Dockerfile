# ==========================================================
# Spring Boot Runtime Template
# ==========================================================

FROM eclipse-temurin:25-jre

LABEL maintainer="Anuj Yadav"
LABEL project="Real-Time-Event-Streaming-Platform"

WORKDIR /app

COPY SERVICE_JAR app.jar

EXPOSE SERVICE_PORT

ENV JAVA_OPTS=""

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
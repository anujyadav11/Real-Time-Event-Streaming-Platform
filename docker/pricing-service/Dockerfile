FROM eclipse-temurin:25-jre

WORKDIR /app
COPY pricing-service/target/pricing-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8089
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

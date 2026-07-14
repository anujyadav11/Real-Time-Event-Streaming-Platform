FROM eclipse-temurin:25-jre

LABEL maintainer="Anuj Yadav"
LABEL application="order-service"

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]
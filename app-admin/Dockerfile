FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/app-admin.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
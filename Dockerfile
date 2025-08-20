FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} classroom-service.jar

ENTRYPOINT ["java", "-jar", "classroom-service.jar"]

EXPOSE 8080
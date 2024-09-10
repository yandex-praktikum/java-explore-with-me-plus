FROM amazoncorretto:21-alpine-jdk
COPY target/*.jar ewm-service.jar
ENTRYPOINT ["java","-jar","/ewm-service.jar"]
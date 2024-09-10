FROM amazoncorretto:21-alpine-jdk
COPY target/*.jar stats-server.jar
ENTRYPOINT ["java","-jar","/stats-server.jar"]
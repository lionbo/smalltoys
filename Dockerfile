FROM openjdk:17-ea-slim
COPY target/*.jar /main.jar

ENTRYPOINT ["java","-jar","/main.jar"]
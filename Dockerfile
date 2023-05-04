FROM openjdk:17-ea-slim
ADD libs/*.jar /main.jar

ENTRYPOINT ["java","-jar","/main.jar"]
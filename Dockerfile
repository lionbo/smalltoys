FROM openjdk:17-ea-slim
ADD target/*.jar /main.jar

ENTRYPOINT ["java","-jar","/main.jar"]
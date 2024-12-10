FROM maven:3-eclipse-temurin-21

WORKDIR /app

COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY src src
COPY .mvn .mvn

RUN mvn package -Dmaven.test.skip=true

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "target/wkshp6-0.0.1-SNAPSHOT.jar"]
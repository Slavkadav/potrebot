FROM maven:3.8.1-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:11-slim
COPY --from=build /home/app/target/potrebot-1.0-SNAPSHOT.jar /usr/local/lib/potrebot.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/potrebot.jar", "-Xmx128m"]

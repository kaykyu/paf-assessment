FROM openjdk:21-jdk-slim AS builder

WORKDIR /src

COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY src src
COPY pom.xml .

RUN chmod +x ./mvnw && \
    ./mvnw package -Dmaven.test.skip=true

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /src/target/assessment-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_DATASOURCE_URL= SPRING_DATA_MONGODB_URI=
ENV PORT=8080

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar
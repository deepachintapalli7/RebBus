
FROM maven:3.9.9-eclipse-temurin-8

WORKDIR /app

COPY . /app/

RUN mvn clean package -DskipTests

CMD ["java", "-jar", "target/RebBus-1.0.jar"]

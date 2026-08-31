FROM eclipse-temurin:8-jdk

WORKDIR /app

COPY . .

RUN mkdir -p out && javac -d out $(find src -name "*.java")

CMD ["java", "-cp", "out", "busbooking.Server"]



FROM eclipse-temurin:8-jdk

WORKDIR /app

COPY . .

RUN mkdir -p out && javac -d out *.java

CMD ["java", "-cp", "out", "busbooking.Server"]

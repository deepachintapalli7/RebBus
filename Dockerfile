FROM eclipse-temurin:8-jdk

WORKDIR /app

COPY . /app/

RUN mkdir -p out
RUN javac -d out *.java

CMD ["java", "-cp", "out", "busbooking.Server"]

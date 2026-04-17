FROM maven:3.9.9-eclipse-temurin-17

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY . .

RUN mvn dependency:resolve

CMD ["mvn", "clean", "verify"]

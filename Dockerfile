FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /app

COPY . .

RUN mvn dependency:resolve

CMD ["mvn", "clean", "verify"]

# Simple build and run
FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

# Copy project
COPY . /app/

# Build fat JAR with all dependencies
RUN mvn -q -DskipTests package

# Run the fat JAR
ENTRYPOINT ["mvn", "-q", "javafx:run"]
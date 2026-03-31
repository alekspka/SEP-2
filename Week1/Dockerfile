# Use Maven image to build the application
FROM maven:latest

# Set working directory inside the container
WORKDIR /app

# Install AWT/Swing runtime dependencies needed for GUI apps on Linux containers.
RUN apt-get update && apt-get install -y --no-install-recommends \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libfreetype6 \
    libfontconfig1 \
 && rm -rf /var/lib/apt/lists/*

# Copy the pom.xml to download dependencies first (caching optimization)
COPY pom.xml /app/

# Copy the entire project to the container
COPY . /app/

# Package the application using Maven
RUN mvn package

# Run the main class from the built JAR
CMD ["java", "-jar", "target/ShoppingCart.jar"]
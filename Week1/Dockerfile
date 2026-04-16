# Build on Linux so JavaFX native libs match the container (amd64).
FROM maven:3.9-eclipse-temurin-21

# OpenJFX Glass/GTK + Prism need X11 client libs at runtime (not in the JRE image).
RUN apt-get update && apt-get install -y --no-install-recommends \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxrandr2 \
    libgtk-3-0 \
    libgl1 \
    fonts-noto-cjk \
    fonts-noto-core \
    && fc-cache -f \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY . /app/

RUN mvn -q -DskipTests clean package

# Keep only Linux JavaFX modules on the module path; Windows jars cause duplicate-module errors.
RUN mkdir -p /app/target/javafx-lib \
    && find /app/target/lib -maxdepth 1 -type f -name 'javafx-*-linux.jar' -exec cp {} /app/target/javafx-lib/ \;

# JavaFX is modular: put Linux OpenJFX JARs on the module path, keep app/runtime deps on the class path.
# Do not use "java -jar" alone — it will not load javafx.* modules.
ENTRYPOINT ["sh", "-c", "exec java -p /app/target/javafx-lib --add-modules javafx.controls,javafx.fxml -cp '/app/target/shoppingcart-1.0.jar:/app/target/lib/*' org.example.ShoppingCartGUI"]

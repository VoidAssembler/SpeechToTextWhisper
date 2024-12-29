# Build stage
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder
WORKDIR /build
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

# Final stage
FROM eclipse-temurin:17-jre-focal

# Install whisper and ffmpeg
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get install -y python3-pip && \
    pip3 install -U openai-whisper && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy application files
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

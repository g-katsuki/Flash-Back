FROM --platform=linux/amd64 openjdk:17-jdk-slim

# curlのインストール
RUN apt-get update && apt-get install -y curl

WORKDIR /app
COPY build/libs/JavaBase-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 
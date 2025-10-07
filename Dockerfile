FROM openjdk:17-jdk-slim
WORKDIR /app
COPY xts-market-feed-service/target/xts-market-feed-service-1.0-SNAPSHOT.jar /app/xts-market-feed-service-1.0-SNAPSHOT.jar
CMD ["java", "-jar", "xts-market-feed-service-1.0-SNAPSHOT.jar"]

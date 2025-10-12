# Build stage
FROM gradle:7.6-jdk11 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle build --no-daemon -x test

# Run stage - AWS 프리티어 최적화 (t2.micro 1GB RAM)
FROM openjdk:11-jre-slim
WORKDIR /app

# 로그 디렉토리 생성
RUN mkdir -p /app/logs

# 애플리케이션 JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 80

# 헬스체크 추가
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:80/actuator/health || exit 1

# JVM 최적화 옵션 (t2.micro 1GB RAM 기준)
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom"

# Spring Boot 프로파일 설정
ENV SPRING_PROFILES_ACTIVE=aws

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

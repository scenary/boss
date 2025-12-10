# 루트 Dockerfile - SERVER 폴더를 빌드 컨텍스트로 사용
# Render에서 사용하기 위한 Dockerfile

FROM gradle:8.9-jdk21 AS builder

WORKDIR /home/gradle/project

# SERVER 폴더의 모든 파일 복사 (settings.gradle, build.gradle 등 포함)
COPY SERVER/ .

# Gradle Wrapper 권한 설정 (Linux용)
RUN chmod +x gradlew || true

# Gradle 빌드 (settings.gradle이 루트에 있어야 함)
RUN ./gradlew build -x test --no-daemon || gradle build -x test --no-daemon

# 실행 단계: JDK 21 JRE 사용 (경량화된 Alpine 이미지)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /home/gradle/project/app/build/libs/app-*.jar app.jar

# Java 버전 확인용 (선택사항)
RUN java -version

EXPOSE 8080

# JDK 21로 Spring Boot 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]


# Spring Boot를 위한 multi-stage Dockerfile
# JDK 21을 명시적으로 사용

# 빌드 단계: JDK 21이 포함된 Gradle 이미지 사용
FROM gradle:8.9-jdk21 AS builder

WORKDIR /home/gradle/project

# Gradle Wrapper 권한 설정
RUN chmod +x gradlew || true

# Gradle 설정 파일 먼저 복사 (의존성 캐싱을 위해)
COPY settings.gradle gradle.properties ./
COPY app/build.gradle ./app/
COPY gradlew ./
COPY gradle ./gradle

# 의존성 다운로드만 먼저 실행 (소스 코드 변경 시 재다운로드 방지)
RUN ./gradlew dependencies --no-daemon || gradle dependencies --no-daemon || true

# 소스 코드 복사 (의존성은 이미 캐시됨)
COPY . .

# Gradle Toolchain이 JDK 21을 사용하도록 보장하며 빌드
# build.gradle의 toolchain 설정이 JDK 21을 사용하도록 되어 있음
# Gradle Wrapper 사용 (로컬 Gradle 버전과 일치)
RUN ./gradlew build -x test --no-daemon || gradle build -x test --no-daemon

# 빌드된 JAR 파일 확인 (디버깅용)
RUN ls -la /home/gradle/project/app/build/libs/ || echo "libs directory not found"

# 실행 단계: JDK 21 JRE 사용 (경량화된 Alpine 이미지)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 빌드된 Spring Boot JAR 파일 복사 (bootJar 태스크로 생성된 실행 가능한 JAR)
# bootJar는 app.jar로 생성됩니다 (app-plain.jar는 일반 JAR, 무시)
COPY --from=builder /home/gradle/project/app/build/libs/app.jar app.jar

# Java 버전 확인용 (선택사항)
RUN java -version

EXPOSE 8080

# JDK 21로 Spring Boot 애플리케이션 실행 (프로덕션 프로필 사용)
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]


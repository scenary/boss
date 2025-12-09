# 🚀 BOSS 프로젝트 셋업 가이드

## 📋 개요

BOSS 프로젝트는 다음으로 구성되어 있습니다:

- **CLIENT**: TypeScript + Node.js (포트 3000)
- **SERVER**: Spring Boot + Gradle (포트 8080)
- **DATABASE**: MySQL (포트 3306)

---

## 🐳 방법 1: Docker Compose로 실행 (추천) ⭐

Docker를 사용하면 **JDK 버전 문제를 완벽히 해결**할 수 있습니다!

### 요구사항
- Docker Desktop 설치: https://www.docker.com/products/docker-desktop
- Docker Compose (일반적으로 Docker Desktop에 포함됨)

### 빠른 시작

```bash
# 프로젝트 루트에서 실행
docker-compose up --build

# 백그라운드에서 실행하려면
docker-compose up -d --build
```

**그래서 끝입니다! 🎉**

### 접속 정보

| 서비스 | URL | 기능 |
|--------|-----|------|
| 서버 | http://localhost:8080/api | Spring Boot REST API |
| 클라이언트 | http://localhost:3000 | TypeScript 애플리케이션 |
| MySQL | localhost:3306 | 데이터베이스 |

### 유용한 Docker 명령어

```bash
# 로그 확인
docker-compose logs -f server
docker-compose logs -f mysql
docker-compose logs -f client

# 특정 서비스만 재시작
docker-compose restart server

# 모든 컨테이너 중지
docker-compose down

# 데이터를 포함한 모든 것 삭제 (깨끗한 새로 시작)
docker-compose down -v
```

---

## 💻 방법 2: 로컬에서 직접 실행

### 요구사항

#### SERVER (Spring Boot)
- **JDK 21** 이상: https://www.oracle.com/java/technologies/downloads/
- **MySQL 8.0** 이상: https://dev.mysql.com/downloads/mysql/

#### CLIENT (TypeScript)
- **Node.js 22** 이상: https://nodejs.org/
- **Yarn**: `npm install -g yarn`

### SERVER 실행

```bash
cd SERVER

# Windows
gradlew.bat clean build
gradlew.bat bootRun

# Mac/Linux
./gradlew clean build
./gradlew bootRun
```

### CLIENT 실행

```bash
cd CLIENT

# 의존성 설치
yarn install

# 개발 서버 실행
yarn dev

# 또는 빌드 후 실행
yarn build
yarn start
```

---

## 🔧 JDK 버전 문제 해결

### ❌ 문제 상황
```
"Unable to find a JDK with matching architecture 21"
또는
"JDK 버전 불일치로 인한 빌드 오류"
```

### ✅ 해결 방법

#### 1️⃣ Docker 사용 (최고의 방법)
```bash
docker-compose up --build
```
JDK를 자동으로 관리하므로 로컬 설치가 필요 없습니다.

#### 2️⃣ JAVA_HOME 환경변수 설정

**Windows:**
1. "시스템 환경 변수 편집" 검색
2. "환경 변수" 버튼 클릭
3. "새로 만들기" → 변수명: `JAVA_HOME`
4. 변수 값: JDK 설치 경로 (예: `C:\Program Files\Java\jdk-21`)
5. PowerShell 재시작 후 확인:
```powershell
$env:JAVA_HOME
java -version
```

**Mac/Linux:**
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

#### 3️⃣ Gradle 래퍼 사용
```bash
cd SERVER
./gradlew --version  # JDK 버전 확인
./gradlew bootRun
```

---

## 📝 파일 구조

```
boss/
├── CLIENT/
│   ├── src/
│   │   └── index.ts              # 메인 TypeScript 파일
│   ├── Dockerfile                 # Docker 이미지 정의
│   ├── package.json               # Node.js 의존성
│   ├── tsconfig.json              # TypeScript 설정
│   └── yarn.lock                  # Yarn 잠금 파일
│
├── SERVER/
│   ├── app/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── com/example/
│   │   │   │   │       └── App.java   # Spring Boot 메인 클래스
│   │   │   │   └── resources/
│   │   │   │       └── application.properties  # Spring 설정
│   │   │   └── test/
│   │   └── build.gradle           # Gradle 빌드 설정
│   ├── gradle/                     # Gradle 래퍼
│   ├── Dockerfile                  # Docker 이미지 정의
│   ├── gradlew                     # Gradle 실행 스크립트
│   └── settings.gradle             # Gradle 프로젝트 설정
│
├── docker-compose.yml              # Docker 서비스 정의
├── .gitignore                      # Git 무시 파일
├── README.md                       # 프로젝트 개요
└── SETUP_GUIDE.md                  # 이 파일
```

---

## 🚨 일반적인 문제 및 해결방법

### MySQL 연결 오류
```
Exception: Connection refused at localhost:3306
```
**해결:**
```bash
# Docker로 실행할 경우
docker-compose restart mysql

# 로컬 MySQL 확인
mysql -u root -p  # 비밀번호: root
```

### Gradle 빌드 오류
```
"Unsupported major.minor version"
```
**해결:**
```bash
cd SERVER
gradlew.bat clean --no-daemon
gradlew.bat build --no-daemon
```

### Yarn 의존성 오류
```bash
# 캐시 초기화
cd CLIENT
yarn cache clean
yarn install
```

### Docker 이미지 빌드 오류
```bash
# Docker 이미지 재구축
docker-compose build --no-cache
docker-compose up
```

---

## 📊 Docker Compose 서비스 설명

### server (Spring Boot)
- **이미지**: `gradle:8.9-jdk21` → `eclipse-temurin:21-jre-alpine`
- **포트**: 8080
- **환경**: MySQL과 자동으로 연결됨

### mysql
- **이미지**: `mysql:8.0`
- **포트**: 3306
- **사용자**: root
- **비밀번호**: root
- **데이터베이스**: boss_db

### client
- **이미지**: `node:22-alpine`
- **포트**: 3000
- **명령어**: `yarn dev`

---

## ✨ 다음 단계

### 클라이언트 확장
```bash
cd CLIENT

# React 추가
yarn add react react-dom
yarn add -D @types/react @types/react-dom

# API 통신 라이브러리
yarn add axios

# UI 프레임워크 (예: Vite)
yarn add -D vite @vitejs/plugin-react
```

### 서버 확장
```bash
cd SERVER/app

# 데이터베이스 엔티티 생성
# src/main/java/com/example/entity/User.java

# 저장소 만들기
# src/main/java/com/example/repository/UserRepository.java

# 컨트롤러 만들기
# src/main/java/com/example/controller/UserController.java
```

### 데이터베이스 초기화
```bash
# MySQL 접속
docker-compose exec mysql mysql -u root -proot boss_db

# 테이블 생성 (예)
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🔗 유용한 링크

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [TypeScript 공식 문서](https://www.typescriptlang.org/)
- [Docker 공식 문서](https://docs.docker.com/)
- [MySQL 공식 문서](https://dev.mysql.com/doc/)
- [Gradle 공식 문서](https://gradle.org/guides/)

---

## 💡 팁

1. **Docker를 사용하세요** - JDK 버전 관리가 자동으로 됩니다.
2. **`.env` 파일 커스터마이징** - `docker-compose.yml`의 `environment` 섹션에서 변수를 수정할 수 있습니다.
3. **핫 리로드** - Yarn과 Spring Boot Devtools는 자동으로 변경사항을 감지합니다.
4. **로그 확인** - 문제 해결을 위해 `docker-compose logs`를 자주 사용하세요.

---

**마지막 업데이트**: 2025년 12월 9일
**추천 방법**: Docker Compose 사용 🐳


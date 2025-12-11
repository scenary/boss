# 버전 관리 가이드

이 문서는 BOSS 프로젝트의 버전 관리 전략과 배포 워크플로우를 설명합니다.

## 📋 버전 번호 규칙

### Semantic Versioning (SemVer) 형식

```
MAJOR.MINOR.PATCH
```

- **MAJOR**: 호환되지 않는 API 변경 (예: v1.0.0 → v2.0.0)
- **MINOR**: 하위 호환되는 기능 추가 (예: v0.1.0 → v0.2.0)
- **PATCH**: 하위 호환되는 버그 수정 (예: v0.1.0 → v0.1.1)

### 현재 프로젝트 버전 규칙

초기 개발 단계이므로 `v0.x.x` 형식을 사용합니다:

```
v0.0.0  # 프로젝트 초기화
v0.0.1  # 첫 번째 패치
v0.1.0  # 첫 번째 기능 추가
v0.2.0  # 두 번째 기능 추가
v0.2.1  # 버그 수정
...
v1.0.0  # 첫 번째 안정 버전 (메이저 릴리즈)
```

## 🚀 배포 워크플로우 (Squash Merge 방식)

### 1. 개발 (master 브랜치)

```bash
# master 브랜치에서 개발
git checkout master

# 기능 개발 및 커밋
git add .
git commit -m "기능: 새로운 기능 추가"
git commit -m "수정: 버그 수정"
git commit -m "리팩토링: 코드 개선"
git push origin master
```

### 2. 배포 전 준비

```bash
# 배포할 커밋 확인
git checkout master
git log deploy..master --oneline

# 변경사항 분석
# - 새 기능이 있나? → MINOR 업데이트
# - 버그 수정만 있나? → PATCH 업데이트
# - API 변경이 있나? → MAJOR 업데이트
```

### 3. CHANGELOG.md 업데이트

`CHANGELOG.md` 파일을 열어서 변경사항을 정리합니다:

```markdown
## [Unreleased]

### Added
- 보스 목록 조회 기능
- 레이드 방 생성 기능

### Changed
- 코드 리팩토링 (성능 개선)

### Fixed
- 로그인 토큰 만료 버그

### Removed
- 사용하지 않는 API 엔드포인트
```

그 다음 `[Unreleased]` 섹션을 새 버전으로 이동:

```markdown
## [v0.2.0] - 2025-12-11

### Added
- 보스 목록 조회 기능
- 레이드 방 생성 기능

### Changed
- 코드 리팩토링 (성능 개선)

### Fixed
- 로그인 토큰 만료 버그
```

### 4. deploy 브랜치에 Squash Merge

```bash
# deploy 브랜치로 이동
git checkout deploy

# master의 모든 변경사항을 하나의 커밋으로 합치기
git merge --squash master

# 버전과 변경사항을 명시한 커밋 메시지 작성
git commit -m "Release v0.2.0

Features:
- 보스 목록 조회 기능
- 레이드 방 생성 기능

Fixes:
- 로그인 토큰 만료 버그

Chores:
- 코드 리팩토링 및 성능 개선

See CHANGELOG.md for details."
```

### 5. 버전 태그 생성 및 배포

```bash
# 버전 태그 생성
git tag -a v0.2.0 -m "Release v0.2.0

- 보스 목록 조회 기능 추가
- 레이드 방 생성 기능 추가
- 로그인 토큰 만료 버그 수정
- 코드 리팩토링 및 성능 개선"

# deploy 브랜치와 태그 push
git push origin deploy
git push origin v0.2.0

# 또는 한번에
git push origin deploy --tags
```

### 6. CHANGELOG.md 커밋 (master에)

```bash
# CHANGELOG.md 변경사항을 master에 커밋
git checkout master
git add CHANGELOG.md
git commit -m "docs: CHANGELOG 업데이트 for v0.2.0"
git push origin master
```

## 📝 CHANGELOG.md 작성 규칙

### 섹션 종류

- **Added**: 새로운 기능
- **Changed**: 기존 기능 변경
- **Deprecated**: 곧 제거될 기능
- **Removed**: 제거된 기능
- **Fixed**: 버그 수정
- **Security**: 보안 관련 변경

### 예시

```markdown
## [v0.2.0] - 2025-12-11

### Added
- 보스 목록 조회 API (`GET /api/bosses`)
- 레이드 방 생성 기능
- 실시간 채팅 기능

### Changed
- 로그인 API 응답 형식 변경 (하위 호환)
- 데이터베이스 쿼리 성능 개선

### Fixed
- 로그인 토큰 만료 시 무한 리다이렉트 버그
- 보스 목록 정렬 오류

### Removed
- 사용하지 않는 `/api/old-endpoint` 제거
```

## 🔄 버전 업데이트 체크리스트

배포 전 확인사항:

- [ ] master 브랜치에서 모든 테스트 통과
- [ ] 배포할 커밋 목록 확인 (`git log deploy..master`)
- [ ] 변경사항 분석 및 버전 결정
- [ ] CHANGELOG.md 업데이트
- [ ] deploy 브랜치에 squash merge
- [ ] 버전 태그 생성
- [ ] deploy 브랜치와 태그 push
- [ ] GitHub Actions 배포 확인
- [ ] 배포 후 동작 확인
- [ ] CHANGELOG.md 변경사항을 master에 커밋

## 📊 버전 결정 가이드

### 질문: 이 변경은 어떤 버전을 올려야 할까?

| 변경 내용 | 버전 업데이트 | 예시 |
|----------|-------------|------|
| 버그 수정만 | PATCH | v0.1.0 → v0.1.1 |
| 새 기능 추가 | MINOR | v0.1.1 → v0.2.0 |
| 기능 + 버그 수정 | MINOR | v0.1.1 → v0.2.0 |
| API 변경 (호환 깨짐) | MAJOR | v0.9.0 → v1.0.0 |

### 중요 규칙

1. **MINOR가 올라가면 PATCH는 0으로 리셋**
   ```
   v0.1.5 → v0.2.0 (v0.2.1이 아님!)
   ```

2. **MAJOR가 올라가면 MINOR와 PATCH는 0으로 리셋**
   ```
   v0.9.5 → v1.0.0 (v1.9.5가 아님!)
   ```

3. **여러 변경사항이 있으면 가장 큰 단위로 올림**
   ```
   버그 수정 + 새 기능 추가 = MINOR 업데이트
   ```

## 🔗 관련 문서

- [CHANGELOG.md](CHANGELOG.md) - 변경사항 기록
- [프로젝트 README](../README.md) - 프로젝트 개요
- [CLIENT_DEPLOYMENT.md](CLIENT_DEPLOYMENT.md) - 클라이언트 배포 가이드
- [문서 목록](README.md) - 모든 문서 목록

---

**마지막 업데이트**: 2025년 12월 11일


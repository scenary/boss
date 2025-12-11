# Changelog

이 프로젝트의 모든 주요 변경사항은 이 파일에 기록됩니다.

형식은 [Keep a Changelog](https://keepachangelog.com/ko/1.0.0/)를 따르며,
이 프로젝트는 [Semantic Versioning](https://semver.org/lang/ko/)을 준수합니다.

## [Unreleased]

### Added
- 

### Changed
- 

### Fixed
- 

### Removed
- 

---

## [v0.1.0] - 2025-12-11

### Added
- 보스 레이드 방 생성 및 관리 기능
- 레이드 방별 채널 관리 기능
- 실시간 WebSocket 통신 (STOMP) 구현
- 채널별 보스 색상 표시 기능 (용 보스: 흑, 진, 묵, 감)
- 채널별 메모 기능
- 사냥 완료 표시 기능
- 레이드 방 완료 처리 기능
- 완료된 레이드 목록 조회 기능
- 레이드 참석 기능 (참석 예정 명단)
- 현재 접속 중 사용자 목록 표시
- 이동중 상태 표시 기능
- Spring Boot 프로필 관리 (dev/prod)
- 환경 변수를 통한 민감 정보 관리

### Changed
- 문서 파일들을 docs/ 폴더로 이동
- UI/UX 개선 (헤더, 버튼 스타일, 명단 표시)
- 채널 생성/삭제 처리 개선
- WebSocket 실시간 업데이트 성능 개선

### Fixed
- 채널 생성 시 다른 사용자에게 즉시 반영되지 않던 문제
- 이동중 표시가 다른 사용자에게 보이지 않던 문제
- 보스 색상 변경이 실시간으로 반영되지 않던 문제
- 레이드 방 삭제/완료 시 목록이 즉시 업데이트되지 않던 문제
- Supabase pgBouncer와의 호환성 문제 (prepared statement)
- 불필요한 Chrome 확장 프로그램 에러 메시지

---

## [v0.0.1] - 2025-12-11

### Added
- 프로젝트 초기화

---

## [v0.0.1] - 2025-12-11

### Added
- 프로젝트 초기화

---

[Unreleased]: https://github.com/your-username/boss/compare/v0.1.0...HEAD
[v0.1.0]: https://github.com/your-username/boss/compare/v0.0.1...v0.1.0
[v0.0.1]: https://github.com/your-username/boss/releases/tag/v0.0.1


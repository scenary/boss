# 클라이언트 배포 가이드 (GitHub Pages)

프론트엔드는 GitHub Pages를 통해 자동으로 배포됩니다.

## 🚀 자동 배포 설정

### 1. GitHub Pages 활성화

1. GitHub 저장소로 이동
2. **Settings** → **Pages** 클릭
3. **Source** 섹션에서:
   - **Deploy from a branch** 선택
   - 또는 **GitHub Actions** 선택 (권장)

### 2. GitHub Secrets 설정 (선택사항)

백엔드 URL을 빌드 시점에 주입하려면:

1. **Settings** → **Secrets and variables** → **Actions** 클릭
2. **New repository secret** 클릭
3. 다음 Secret 추가:

```
이름: VITE_BACKEND_URL
값: https://your-backend-server.com:8080
```

예시:
- 로컬 개발: `http://localhost:8080`
- 프로덕션: `https://api.yourdomain.com:8080`

### 3. 배포 확인

1. **CLIENT** 폴더의 파일을 수정하고 `deploy` 브랜치에 push
2. **Actions** 탭에서 워크플로우 실행 확인
3. 배포 완료 후 접속:
   - `https://yourusername.github.io/boss`
   - 또는 저장소 설정에서 커스텀 도메인 설정 가능

## 📝 워크플로우 동작 방식

1. **트리거**: `deploy` 브랜치의 `CLIENT/**` 경로 파일이 변경되면 자동 실행
2. **빌드**: Node.js 22로 의존성 설치 및 빌드
3. **배포**: 빌드된 `CLIENT/dist` 폴더를 GitHub Pages에 배포

## 🔧 수동 배포

필요시 **Actions** 탭에서 **Deploy Client** 워크플로우를 선택하고 **Run workflow** 버튼을 클릭하여 수동으로 배포할 수 있습니다.

## ⚠️ 주의사항

- 백엔드 URL은 빌드 시점에 환경 변수로 주입됩니다
- `VITE_BACKEND_URL` Secret이 없으면 기본값 `http://localhost:8080` 사용
- 프로덕션 배포 시 반드시 실제 백엔드 URL을 설정하세요

## 🔗 백엔드 배포

백엔드는 별도로 구성하여 배포합니다. (추후 백엔드 배포 워크플로우 추가 예정)


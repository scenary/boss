# .env 파일을 읽어서 환경 변수로 설정하고 Spring Boot 실행
# 특수문자(! 등)가 포함된 비밀번호도 정상 처리
$envFile = Join-Path $PSScriptRoot "..\.env"

if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        $line = $_.Trim()
        if ($line -and -not $line.StartsWith("#")) {
            $parts = $line -split "=", 2
            if ($parts.Length -eq 2) {
                $key = $parts[0].Trim()
                $value = $parts[1].Trim()
                # 특수문자 포함 값도 정상 처리
                [Environment]::SetEnvironmentVariable($key, $value, "Process")
            }
        }
    }
}

cd $PSScriptRoot
.\gradlew.bat bootRun


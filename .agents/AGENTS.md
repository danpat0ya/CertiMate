# Antigravity Custom Rules for CertiMate

## 프로젝트 준비 단축어 (Project Setup Shortcut)
사용자가 "프로젝트 준비" 또는 이에 준하는 명령을 내리면 다음 프로세스를 자동으로 진행하십시오:

1. **백엔드 서버 기동**:
   - 위치: `C:\Users\you03\IntelliJ-workspace\certimate`
   - 작업: 8080 포트를 점유하고 있는 기존 프로세스를 강제 종료하고, `gradlew bootRun` 명령어로 서버를 백그라운드 태스크로 구동합니다.
   - 명령어: `Stop-Process -Id (Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue).OwningProcess -Force -ErrorAction SilentlyContinue; .\gradlew.bat bootRun`

2. **프론트엔드 서버 기동**:
   - 위치: `C:\Users\you03\my-app2`
   - 작업: `npm.cmd start` 명령어를 사용하여 프론트엔드 서버를 백그라운드 태스크로 구동합니다.
   - 명령어: `npm.cmd start`

3. **로딩 확인 및 완료 보고**:
   - 두 서버가 백그라운드에서 정상적으로 구동(백엔드 Tomcat 8080 포트 시작, 프론트엔드 Compiled successfully 로컬 3000 포트 시작)되었는지 로그 파일을 모니터링하여 사용자에게 완료 상태를 보고합니다.

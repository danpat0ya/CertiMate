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

## 깃허브 업로드 단축어 (GitHub Upload Shortcut)
사용자가 "깃허브에 올려줘" 또는 이에 준하는 명령을 내리면 다음 프로세스를 자동으로 진행하십시오:
1. **커밋 메시지 질문**: 즉시 작업을 수행하지 말고, "어떤 제목(커밋 메시지)으로 깃허브에 올려드릴까요?"라고 사용자에게 물어보고 응답을 대기합니다.
2. **깃허브 업로드**: 사용자가 커밋 메시지를 알려주면, 다음 작업을 수행합니다:
   - 프론트엔드(`C:\Users\you03\my-app2`): `git add .`, `git commit -m "사용자입력제목"`, `git push origin ysc_f` 수행
   - 백엔드(`C:\Users\you03\IntelliJ-workspace\certimate`): `git add .`, `git commit -m "사용자입력제목"`, `git push origin ysc_b` 수행
3. **충돌 해결**: 만약 백엔드나 프론트엔드 원격 저장소와 커밋 히스토리가 달라 충돌이 난다면, 상황에 맞게 `pull --rebase` 혹은 `--allow-unrelated-histories` 등으로 해결하거나 사용자에게 보고한 뒤 푸시를 완료합니다.

package com.certimate.controller;

import com.certimate.dto.QuizHistoryRequest;
import com.certimate.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // CORS 문제 방지용 (프론트엔드 포트)
public class ExamController {

    private final ExamService examService;

    @PostMapping("/save-history")
    public ResponseEntity<String> saveHistory(
            @RequestBody List<QuizHistoryRequest> historyPayload
            // 실제 서비스에서는 @AuthenticationPrincipal UserDetails user 등을 통해 로그인한 유저 정보를 가져옵니다.
    ) {
        try {
            // TODO: 세션이나 JWT 토큰에서 현재 로그인한 사용자의 ID를 가져와야 합니다.
            // 지금은 테스트를 위해 임시로 userId 1을 고정값으로 넘깁니다.
            Long currentUserId = 1L;

            examService.saveQuizHistory(currentUserId, historyPayload);

            return ResponseEntity.ok("오답노트가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            e.printStackTrace(); // 서버 로그에서 정확한 500 에러 원인 확인용
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }
}
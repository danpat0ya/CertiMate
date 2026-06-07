package com.certimate.controller;

import com.certimate.domain.AiLearn;
import com.certimate.dto.QuizHistoryDto;
import com.certimate.service.MockExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // CORS 허용 (개발 환경용)
@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class MockExamController {

    private final MockExamService mockExamService;

    // 1. 기존 모의고사 출제 API
    @GetMapping("/{certId}/mock")
    public ResponseEntity<List<AiLearn>> getMockExam(@PathVariable Long certId) {
        return ResponseEntity.ok(mockExamService.generateMockExam(certId));
    }

    // 2. [추가] 모의고사 결과 저장 (오답노트용) API
    @PostMapping("/history")
    public ResponseEntity<String> saveExamHistory(@RequestBody List<QuizHistoryDto> historyList) {
        // 현재 로그인 기능이 없으므로 임시로 userId를 1번으로 고정하여 저장
        Long userId = 1L;
        mockExamService.saveHistory(userId, historyList);
        return ResponseEntity.ok("오답노트 저장 완료");
    }
}
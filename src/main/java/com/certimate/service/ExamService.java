package com.certimate.service;

import com.certimate.domain.UserQuizHistory;
import com.certimate.dto.QuizHistoryRequest;
import com.certimate.repository.UserQuizHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final UserQuizHistoryRepository historyRepository;

    @Transactional
    public void saveQuizHistory(Long userId, List<QuizHistoryRequest> requests) {

        List<UserQuizHistory> histories = requests.stream()
                .map(req -> UserQuizHistory.builder()
                        .userId(userId) // ★ 핵심: DB 필수값이므로 반드시 세팅해야 함
                        .learnId(req.getLearnId())
                        .userAnswer(req.getUserAnswer())
                        .isCorrect(req.getIsCorrect())
                        .build())
                .collect(Collectors.toList());

        // 여러 건의 데이터를 한 번에 저장 (배치 인서트 효과)
        historyRepository.saveAll(histories);
    }
}
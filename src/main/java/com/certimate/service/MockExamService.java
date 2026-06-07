package com.certimate.service;

import com.certimate.domain.AiLearn;
import com.certimate.domain.UserQuizHistory;
import com.certimate.dto.QuizHistoryDto;
import com.certimate.repository.AiLearnRepository;
import com.certimate.repository.UserQuizHistoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MockExamService {

    private final AiLearnRepository aiLearnRepository;

    // 🚨 바로 이 줄이 추가되어야 합니다! (새로 만든 Repository를 주입받음)
    private final UserQuizHistoryRepository historyRepository;

    @PostConstruct
    public void checkDatabaseStatus() {
        long count = aiLearnRepository.count();
        System.out.println("\n==================================================");
        System.out.println("🚨 [디버깅] 현재 AI_LEARN 테이블의 데이터 개수: " + count + "개");
        System.out.println("==================================================\n");
    }

    @Transactional(readOnly = true)
    public List<AiLearn> generateMockExam(Long certId) {
        List<AiLearn> mockExam = new ArrayList<>();

        // 1과목, 2과목, 3과목 순서대로 반복
        for (int subjectNum = 1; subjectNum <= 3; subjectNum++) {
            // 1. 해당 자격증(certId)과 과목(subjectNum)의 전체 문제 가져오기
            List<AiLearn> allQuestions = aiLearnRepository.findByCertIdAndSubjectNum(certId, subjectNum);

            // 2. 전체 문제를 무작위로 섞기
            Collections.shuffle(allQuestions);

            // 3. 앞에서부터 딱 20개만 잘라서 모의고사 리스트에 추가
            int limit = Math.min(allQuestions.size(), 20);
            mockExam.addAll(allQuestions.subList(0, limit));
        }

        return mockExam;
    }

    @Transactional
    public void saveHistory(Long userId, List<QuizHistoryDto> historyList) {
        for (QuizHistoryDto dto : historyList) {
            UserQuizHistory history = new UserQuizHistory();
            history.setUserId(userId);
            history.setLearnId(dto.getLearnId());
            history.setUserAnswer(dto.getUserAnswer());
            history.setIsCorrect(dto.getIsCorrect());

            // 이제 historyRepository를 정상적으로 사용할 수 있습니다!
            historyRepository.save(history);
        }
    }
}
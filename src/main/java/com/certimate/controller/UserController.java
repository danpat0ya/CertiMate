package com.certimate.controller;

import com.certimate.domain.User;
import com.certimate.domain.UserLearnLog;
import com.certimate.domain.UserScrap;
import com.certimate.repository.UserCertificationRepository;
import com.certimate.repository.UserLearnLogRepository;
import com.certimate.repository.UserRepository;
import com.certimate.repository.UserScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import com.certimate.domain.ExamSchedule;
import com.certimate.domain.UserQuizHistory;
import com.certimate.repository.ExamScheduleRepository;
import com.certimate.repository.UserQuizHistoryRepository;
import com.certimate.repository.AiLearnRepository;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserCertificationRepository userCertificationRepository;
    private final UserScrapRepository userScrapRepository;
    private final UserLearnLogRepository userLearnLogRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final UserQuizHistoryRepository userQuizHistoryRepository;
    private final AiLearnRepository aiLearnRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        return userRepository.findByEmail(principal.getName()).map(user -> {
            int certCount = userCertificationRepository.countByUser_Id(user.getId());
            
            List<UserScrap> scraps = userScrapRepository.findByUser_Id(user.getId());
            int scrapCount = scraps.size();
            List<ScrapDto> scrapDtos = scraps.stream()
                    .map(s -> new ScrapDto(s.getCertification().getCertName(), s.getId().toString()))
                    .collect(Collectors.toList());

            List<UserLearnLog> logs = userLearnLogRepository.findByUser_Id(user.getId());
            int totalStudyMin = logs.stream().mapToInt(UserLearnLog::getStudyTimeMin).sum();
            double avgCorrectRate = logs.stream().mapToDouble(UserLearnLog::getCorrectRate).average().orElse(0.0);

            // 1. Heatmap Data (최근 1년 퀴즈 기록)
            LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
            List<UserQuizHistory> quizHistory = userQuizHistoryRepository.findByUserIdAndSolvedAtAfter(user.getId(), oneYearAgo);
            
            Map<String, Long> dateCounts = quizHistory.stream()
                .filter(q -> q.getSolvedAt() != null)
                .collect(Collectors.groupingBy(
                    q -> q.getSolvedAt().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    Collectors.counting()
                ));
            
            List<HeatmapDto> heatmapData = dateCounts.entrySet().stream()
                .map(e -> new HeatmapDto(e.getKey(), e.getValue().intValue()))
                .collect(Collectors.toList());

            // 2. Target Exam D-Day
            TargetExamDto targetExam = null;
            UserLearnLog recentLog = logs.stream()
                .max(Comparator.comparing(UserLearnLog::getLastStudiedAt))
                .orElse(null);
                
            if (recentLog != null) {
                List<ExamSchedule> schedules = examScheduleRepository.findByCertification_IdAndExamDateAfterOrderByExamDateAsc(
                        recentLog.getCertification().getId(), LocalDate.now().minusDays(1));
                
                if (!schedules.isEmpty()) {
                    ExamSchedule upcoming = schedules.get(0);
                    long dDay = ChronoUnit.DAYS.between(LocalDate.now(), upcoming.getExamDate());
                    targetExam = new TargetExamDto(
                        recentLog.getCertification().getCertName(),
                        upcoming.getExamType(),
                        upcoming.getExamDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        dDay,
                        String.format("%.0f", recentLog.getCorrectRate())
                    );
                }
            }

            return ResponseEntity.ok(new DashboardResponse(
                    certCount,
                    scrapCount,
                    scrapDtos,
                    totalStudyMin / 60 + "h",
                    String.format("%.0f%%", avgCorrectRate),
                    heatmapData,
                    targetExam
            ));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/quiz-history")
    public ResponseEntity<?> getQuizHistory(@RequestParam String date, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        return userRepository.findByEmail(principal.getName()).map(user -> {
            LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDateTime start = parsedDate.atStartOfDay();
            LocalDateTime end = parsedDate.atTime(23, 59, 59, 999999999);

            List<UserQuizHistory> history = userQuizHistoryRepository.findByUserIdAndSolvedAtBetween(user.getId(), start, end);
            history.sort(Comparator.comparing(UserQuizHistory::getSolvedAt));

            List<QuizSessionDto> sessions = new ArrayList<>();
            List<QuizHistoryDto> currentSessionRecords = new ArrayList<>();
            LocalDateTime prevTime = null;
            int sessionNum = 1;

            for (UserQuizHistory h : history) {
                if (prevTime != null && ChronoUnit.MINUTES.between(prevTime, h.getSolvedAt()) > 1) {
                    if (!currentSessionRecords.isEmpty()) {
                        sessions.add(new QuizSessionDto(sessionNum++, currentSessionRecords.get(0).solvedAtStr(), new ArrayList<>(currentSessionRecords)));
                        currentSessionRecords.clear();
                    }
                }
                
                com.certimate.domain.AiLearn learn = aiLearnRepository.findById(h.getLearnId()).orElse(null);
                if (learn != null) {
                    currentSessionRecords.add(new QuizHistoryDto(
                        learn.getLearnId(),
                        learn.getQuestion(),
                        learn.getOptions(),
                        h.getUserAnswer(),
                        learn.getAnswer(),
                        h.getIsCorrect(),
                        learn.getExplanation(),
                        h.getSolvedAt().format(DateTimeFormatter.ofPattern("a h:mm"))
                    ));
                }
                prevTime = h.getSolvedAt();
            }
            if (!currentSessionRecords.isEmpty()) {
                sessions.add(new QuizSessionDto(sessionNum, currentSessionRecords.get(0).solvedAtStr(), currentSessionRecords));
            }

            return ResponseEntity.ok(sessions);
        }).orElse(ResponseEntity.notFound().build());
    }

    public record ScrapDto(String title, String id) {}
    public record HeatmapDto(String date, int count) {}
    public record TargetExamDto(String certName, String examType, String examDate, long dDay, String achievementRate) {}
    public record QuizHistoryDto(Long learnId, String question, String options, String userAnswer, String correctAnswer, boolean isCorrect, String explanation, String solvedAtStr) {}
    public record QuizSessionDto(int sessionNum, String timeLabel, List<QuizHistoryDto> records) {}
    
    public record DashboardResponse(
            int certCount,
            int scrapCount,
            List<ScrapDto> recentScraps,
            String totalStudyTime,
            String cbtAccuracy,
            List<HeatmapDto> heatmapData,
            TargetExamDto targetExam
    ) {}
}

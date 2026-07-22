package com.certimate.repository;

import com.certimate.domain.ExamSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {
    List<ExamSchedule> findByCertification_IdAndExamDateAfterOrderByExamDateAsc(Long certId, LocalDate date);
}

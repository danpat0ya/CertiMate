package com.certimate.repository;

import com.certimate.domain.UserQuizHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQuizHistoryRepository extends JpaRepository<UserQuizHistory, Long> {
}
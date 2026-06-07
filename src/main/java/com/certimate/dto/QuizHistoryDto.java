package com.certimate.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizHistoryDto {
    private Long learnId;
    private String userAnswer;
    private Boolean isCorrect;
}
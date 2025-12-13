package com.example.server.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDetailResponse {
    private UUID id;
    private Double totalScore;
    private String status;          // "GRADED" or "PENDING"
    private String teacherFeedback;
    private List<QuestionResultResponse> answers;
}

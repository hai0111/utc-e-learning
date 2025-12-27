package com.example.server.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class QuizSubmissionResponse {

    private UUID attemptId;

    private Double totalScore;

    private Double maxScore;

    private String statusMessage;
}

package com.example.server.service;

import com.example.server.response.QuizAttemptDetailResponse;

import java.util.UUID;

public interface QuizAttemptService {
    com.example.server.response.QuizAttemptDetailResponse getAttemptResult(UUID attemptId);
}

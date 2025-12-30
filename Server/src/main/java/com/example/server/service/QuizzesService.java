package com.example.server.service;

import com.example.server.model.Quizzes;
import com.example.server.request.QuizSubmissionRequest;
import com.example.server.request.QuizzesRequest;
import com.example.server.response.ApiResponse;
import com.example.server.response.QuizzesResponse;

import java.util.UUID;

public interface QuizzesService {

    Quizzes createQuizzes(QuizzesRequest quizzesRequest);

    Quizzes updateQuizzes(Quizzes quizzes, QuizzesRequest quizzesRequest);

    void deleteQuizIfUnused(UUID quizId);

    ApiResponse<Object> submitQuizzes(QuizSubmissionRequest quizSubmissionRequest, UUID quizId, UUID lessonId);

    ApiResponse<QuizzesResponse> getQuizForStudentToTake(UUID quizId);
}

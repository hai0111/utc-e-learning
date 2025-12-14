package com.example.server.service;

import com.example.server.model.Quizzes;
import com.example.server.request.QuizzesRequest;

import java.util.UUID;

public interface QuizzesService {

    Quizzes createQuizzes(QuizzesRequest quizzesRequest);

    Quizzes updateQuizzes(Quizzes quizzes, QuizzesRequest quizzesRequest);

    void deleteQuizIfUnused(UUID quizId);
}

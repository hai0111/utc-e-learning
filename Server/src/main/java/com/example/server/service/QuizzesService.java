package com.example.server.service;

import com.example.server.model.Quizzes;
import com.example.server.request.QuizzesRequest;

public interface QuizzesService {

    Quizzes createQuizzes(QuizzesRequest quizzesRequest);
}

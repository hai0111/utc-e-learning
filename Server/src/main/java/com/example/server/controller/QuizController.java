package com.example.server.controller;

import com.example.server.request.QuizSubmissionRequest;
import com.example.server.service.QuizzesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizzesService quizzesService;

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable UUID quizId,
                                        @RequestParam UUID lessonId,
                                        @RequestBody @Valid QuizSubmissionRequest request) {
        return ResponseEntity.ok(quizzesService.submitQuizzes(request, quizId, lessonId));
    }
}

package com.example.server.controller;

import com.example.server.response.ApiResponse;
import com.example.server.response.QuizAttemptDetailResponse;
import com.example.server.service.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {
    private final QuizAttemptService quizAttemptService;

    @GetMapping("/quiz-attempts/{id}")
    public ResponseEntity<ApiResponse<QuizAttemptDetailResponse>> viewMyQuizAttempt(@PathVariable UUID id) {

        QuizAttemptDetailResponse data = quizAttemptService.getAttemptResult(id);

        ApiResponse<QuizAttemptDetailResponse> response = new ApiResponse<>(200, "Retrieved quiz attempt details successfully", data);

        return ResponseEntity.ok(response);
    }
}

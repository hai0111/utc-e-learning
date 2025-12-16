package com.example.server.controller;

import com.example.server.response.ApiResponse;
import com.example.server.response.CourseProgressResponse;
import com.example.server.response.QuizAttemptDetailResponse;
import com.example.server.security.service.UserDetailsImpl;
import com.example.server.service.QuizAttemptService;
import com.example.server.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final StudentService studentService;

    @GetMapping("/quiz-attempts/{id}")
    public ResponseEntity<ApiResponse<QuizAttemptDetailResponse>> viewMyQuizAttempt(@PathVariable UUID id) {

        QuizAttemptDetailResponse data = quizAttemptService.getAttemptResult(id);

        ApiResponse<QuizAttemptDetailResponse> response = new ApiResponse<>(200, "Retrieved quiz attempt details successfully", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/{courseId}/progress")
    public ResponseEntity<ApiResponse<CourseProgressResponse>> getMyCourseProgress(@PathVariable UUID courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID currentUserId = userDetails.getId();

        CourseProgressResponse data = studentService.getCourseProgress(courseId, currentUserId);

        return ResponseEntity.ok(new ApiResponse<>(200, "Get course progress successfully", data));
    }
}

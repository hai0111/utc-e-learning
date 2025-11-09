package com.example.server.controller;

import com.example.server.request.LessonRequest;
import com.example.server.request.UpdateLessonBatchRequest;
import com.example.server.response.ApiResponse;
import com.example.server.response.BatchUpdateResponse;
import com.example.server.response.LessonResponse;
import com.example.server.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class LesssonController {

    @Autowired
    private LessonService lessonService;

    @GetMapping("/{courseId}/lessons")
    public ResponseEntity<?> getListLessons(@PathVariable UUID courseId) {
        List<LessonResponse> lessons = lessonService.getListLessons(courseId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/{courseId}/lessons/{lessonId}")
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonById(@PathVariable UUID courseId, @PathVariable UUID lessonId) {

        ApiResponse<LessonResponse> response = lessonService.getLesson(courseId, lessonId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping(value = "/{courseId}/lessons/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> createLesson(@PathVariable UUID courseId, @ModelAttribute @Valid LessonRequest lessonRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            ApiResponse<Object> response = new ApiResponse<>(400, Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), null);
            return ResponseEntity.status(400).body(response);
        }

        ApiResponse<Object> response = lessonService.createLesson(lessonRequest, courseId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PutMapping("/{courseId}/lessons/edit/{lessonId}")
    public ResponseEntity<ApiResponse<Object>> editLesson(@PathVariable UUID courseId, @PathVariable UUID lessonId, @RequestBody @Valid LessonRequest lessonRequest) {
        ApiResponse<Object> response = lessonService.editLesson(lessonRequest, courseId, lessonId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PutMapping("/{courseId}/lessons/hidden/{lessonId}")
    public ResponseEntity<ApiResponse<Object>> hiddenLesson(@PathVariable UUID courseId, @PathVariable UUID lessonId) {
        ApiResponse<Object> response = lessonService.hiddenLesson(courseId, lessonId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{courseId}/lessons/delete/{lessonId}")
    public ResponseEntity<ApiResponse<Object>> deleteLesson(@PathVariable UUID courseId, @PathVariable UUID lessonId) {
        ApiResponse<Object> response = lessonService.deleteLesson(courseId, lessonId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PutMapping("/{courseId}/lessons/batch-update")
    public ResponseEntity<ApiResponse<BatchUpdateResponse>> updateLessonBatch(@PathVariable UUID courseId, @RequestBody @Valid UpdateLessonBatchRequest request) {
        ApiResponse<BatchUpdateResponse> response = lessonService.updateLessonBatch(request, courseId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{courseId}/lessons/search")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> quickSearchLessonsByTitleAndCourse(@PathVariable UUID courseId, @RequestParam String title) {
        ApiResponse<List<LessonResponse>> response = lessonService.searchLessonsByTitleAndCourse(title, courseId);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}

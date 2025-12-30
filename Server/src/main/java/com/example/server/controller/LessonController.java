package com.example.server.controller;

import com.example.server.request.LessonRequest;
import com.example.server.request.UpdateLessonBatchRequest;
import com.example.server.request.UpdateProgressRequest;
import com.example.server.response.ApiResponse;
import com.example.server.response.BatchUpdateResponse;
import com.example.server.response.LessonResponse;
import com.example.server.security.service.UserDetailsImpl;
import com.example.server.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/{courseId}/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping()
    public ResponseEntity<?> getListLessons(@PathVariable UUID courseId) {
        ApiResponse<List<LessonResponse>> lessons = lessonService.getListLessons(courseId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<?> getLessonById(@PathVariable UUID courseId, @PathVariable UUID lessonId) {
        ApiResponse<LessonResponse> response = lessonService.getLesson(courseId, lessonId);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createLesson(
            @PathVariable UUID courseId,
            @RequestPart("data") @Valid LessonRequest lessonRequest,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        ApiResponse<Object> response = lessonService.createLesson(lessonRequest, courseId, file);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/edit/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editLesson(
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @RequestPart("data") @Valid LessonRequest lessonRequest,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        ApiResponse<Object> response = lessonService.editLesson(lessonRequest, courseId, lessonId, file);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/hide/{lessonId}")
    public ResponseEntity<?> hideLesson(@PathVariable UUID courseId, @PathVariable UUID lessonId) {
        ApiResponse<Object> response = lessonService.hideLesson(courseId, lessonId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{lessonId}")
    public ResponseEntity<?> deleteLesson(@PathVariable UUID courseId, @PathVariable UUID lessonId) {
        ApiResponse<Object> response = lessonService.deleteLesson(courseId, lessonId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/batch-update")
    public ResponseEntity<?> updateLessonBatch(@PathVariable UUID courseId, @RequestBody @Valid UpdateLessonBatchRequest request) {
        ApiResponse<BatchUpdateResponse> response = lessonService.updateLessonBatch(request, courseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchLessonsByTitleAndCourse(@PathVariable UUID courseId, @RequestParam String title) {
        ApiResponse<List<LessonResponse>> response = lessonService.searchLessonsByTitleAndCourse(title, courseId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/progress")
    public ResponseEntity<ApiResponse<String>> updateProgress(@RequestBody @Valid UpdateProgressRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID currentUserId = userDetails.getId();

        lessonService.updateProgress(request, currentUserId);

        return ResponseEntity.ok(new ApiResponse<>(200, "Progress updated successfully", null));
    }
}
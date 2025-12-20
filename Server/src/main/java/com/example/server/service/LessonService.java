package com.example.server.service;

import com.example.server.request.LessonRequest;
import com.example.server.request.UpdateLessonBatchRequest;
import com.example.server.request.UpdateProgressRequest;
import com.example.server.response.ApiResponse;
import com.example.server.response.BatchUpdateResponse;
import com.example.server.response.LessonResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface LessonService {

    ApiResponse<List<LessonResponse>> getListLessons(UUID courseId);

    ApiResponse<LessonResponse> getLesson(UUID courseId, UUID lessonId);

    ApiResponse<Object> createLesson(LessonRequest lessonRequest, UUID courseId, MultipartFile file);

    ApiResponse<Object> editLesson(LessonRequest lessonRequest, UUID courseId, UUID lessonId);

    ApiResponse<List<LessonResponse>> searchLessonsByTitleAndCourse(String title, UUID courseId);

    ApiResponse<Object> hideLesson(UUID courseId, UUID lessonId);

    ApiResponse<Object> deleteLesson(UUID courseId, UUID lessonId);

    ApiResponse<BatchUpdateResponse> updateLessonBatch(UpdateLessonBatchRequest request, UUID courseId);

    void updateProgress(UpdateProgressRequest request, UUID userId);
}

package com.example.server.service;

import com.example.server.request.LessonRequest;
import com.example.server.request.UpdateLessonBatchRequest;
import com.example.server.response.BatchUpdateResponse;
import com.example.server.response.LessonResponse;
import com.example.server.response.MessageResponse;

import java.util.List;
import java.util.UUID;

public interface LessonService {
    List<LessonResponse> getListLessons(UUID courseId);
    LessonResponse getLesson(UUID courseId, UUID lessonId);
    MessageResponse createLesson(LessonRequest lessonRequest, UUID courseId);
    MessageResponse editLesson(LessonRequest lessonRequest, UUID courseId, UUID lessonId);
    MessageResponse hiddenLesson(UUID courseId, UUID lessonId);
    MessageResponse deleteLesson(UUID courseId, UUID lessonId);
    BatchUpdateResponse updateLessonBatch(UpdateLessonBatchRequest request, UUID courseId);
}

package com.example.server.service;

import com.example.server.response.CourseProgressResponse;

import java.util.UUID;

public interface StudentService {
    CourseProgressResponse getCourseProgress(UUID courseId, UUID studentId);
}

package com.example.server.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class CourseProgressResponse {
    // --- Course Info ---
    private UUID courseId;
    private String courseTitle;
    private String description;
    private String updatedAt;

    // --- Progress Info ---
    private Integer totalLessons;
    private Integer completedLessons;
    private Double progressPercent;

    // --- Instructor Info ---
    private String instructorName;
    private String instructorPosition;
    private Long instructorTotalCourses;
    private Long instructorTotalStudents;
}

package com.example.server.response;

import com.example.server.dto.LessonsDto;
import com.example.server.enums.LessonType;
import com.example.server.model.Lessons;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LessonResponse {
    private UUID id;
    private UUID courseId;
    private String title;
    private String url;
    private LessonType type;
    private Boolean isActive;
    private Boolean isDelete;
    private Integer orderIndex;
    private UUID createdBy;
    private String createdByName;
    private UUID updatedBy;
    private String updatedByName;
    private QuizzesResponse quizzesResponse;
    private Double currentPercent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updated_at")
    private Date updatedAt;

    public static LessonResponse convertToResponse(Lessons lesson) {
        LessonResponse response = new LessonResponse();
        response.setId(lesson.getId());
        response.setCourseId(lesson.getCourse().getId());
        response.setTitle(lesson.getTitle());
        response.setUrl(lesson.getUrl());
        response.setType(lesson.getLessonType());
        response.setIsActive(lesson.getIsActive());
        response.setIsDelete(lesson.getIsDelete());
        response.setOrderIndex(lesson.getOrderIndex());
        response.setCreatedAt(lesson.getCreatedAt());
        response.setUpdatedAt(lesson.getUpdatedAt());

        // Set createdBy user info
        if (lesson.getCreatedBy() != null) {
            response.setCreatedBy(lesson.getCreatedBy().getId());
            response.setCreatedByName(lesson.getCreatedBy().getName()); // Assuming Users has getFullName()
        }

        // Set updatedBy user info
        if (lesson.getUpdatedBy() != null) {
            response.setUpdatedBy(lesson.getUpdatedBy().getId());
            response.setUpdatedByName(lesson.getUpdatedBy().getName());
        }

        if (lesson.getQuizzes() != null) {
            response.setQuizzesResponse(QuizzesResponse.getQuizzesResponse(lesson.getQuizzes()));
        } else {
            response.setQuizzesResponse(null);
        }

        return response;
    }

    public static LessonResponse convertToResponse(LessonsDto lessonsDto) {
        LessonResponse response = new LessonResponse();
        response.setId(lessonsDto.getId());
        response.setCourseId(lessonsDto.getCourseId());
        response.setTitle(lessonsDto.getTitle());
        response.setUrl(lessonsDto.getUrl());
        response.setType(LessonType.valueOf(lessonsDto.getLessonType()));
        response.setIsActive(lessonsDto.getIsActive());
        response.setIsDelete(lessonsDto.getIsDelete());
        response.setOrderIndex(lessonsDto.getOrderIndex());
        response.setCreatedAt(lessonsDto.getCreatedAt());
        response.setUpdatedAt(lessonsDto.getUpdatedAt());
        response.setCreatedBy(lessonsDto.getCreatedById());
        response.setCreatedByName(lessonsDto.getCreatedByName());
        response.setUpdatedBy(lessonsDto.getUpdatedById());
        response.setUpdatedByName(lessonsDto.getUpdatedByName());
        response.setCurrentPercent(lessonsDto.getCurrentPercent());
        return response;
    }
}

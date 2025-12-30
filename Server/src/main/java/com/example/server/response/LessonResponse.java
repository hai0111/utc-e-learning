package com.example.server.response;

import com.example.server.dto.LessonsDto;
import com.example.server.enums.LessonType;
import com.example.server.model.Lessons;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.List;
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
    private List<QuizzesResponse> quizzesResponseList;
    private Double currentPercent;
    private String createdAt;
    private String updatedAt;

    public static LessonResponse convertToResponse(Lessons lesson) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        LessonResponse response = new LessonResponse();
        response.setId(lesson.getId());
        response.setCourseId(lesson.getCourse().getId());
        response.setTitle(lesson.getTitle());
        response.setUrl(lesson.getUrl());
        response.setType(lesson.getLessonType());
        response.setIsActive(lesson.getIsActive());
        response.setIsDelete(lesson.getIsDelete());
        response.setOrderIndex(lesson.getOrderIndex());
        response.setCreatedAt(formatter.format(lesson.getCreatedAt()));
        response.setUpdatedAt(formatter.format(lesson.getUpdatedAt()));

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
            QuizzesResponse qr = QuizzesResponse.getQuizzesResponse(lesson.getQuizzes());
            response.setQuizzesResponseList(List.of(qr));
        } else {
            response.setQuizzesResponseList(null);
        }

        return response;
    }

    public static LessonResponse convertToResponse(LessonsDto lessonsDto) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        LessonResponse response = new LessonResponse();
        response.setId(lessonsDto.getId());
        response.setCourseId(lessonsDto.getCourseId());
        response.setTitle(lessonsDto.getTitle());
        response.setUrl(lessonsDto.getUrl());
        response.setType(LessonType.valueOf(lessonsDto.getLessonType()));
        response.setIsActive(lessonsDto.getIsActive());
        response.setIsDelete(lessonsDto.getIsDelete());
        response.setOrderIndex(lessonsDto.getOrderIndex());
        response.setCreatedAt(lessonsDto.getCreatedAt().toString());
        response.setUpdatedAt(lessonsDto.getUpdatedAt().toString());
        response.setCreatedAt(formatter.format(lessonsDto.getCreatedAt()));
        response.setUpdatedAt(formatter.format(lessonsDto.getUpdatedAt()));
        response.setCreatedBy(lessonsDto.getCreatedById());
        response.setCreatedByName(lessonsDto.getCreatedByName());
        response.setUpdatedBy(lessonsDto.getUpdatedById());
        response.setUpdatedByName(lessonsDto.getUpdatedByName());
        response.setCurrentPercent(lessonsDto.getCurrentPercent());
        if (lessonsDto.getQuizId() != null) {
            QuizzesResponse quizzesResponse = new QuizzesResponse();
            quizzesResponse.setQuizId(lessonsDto.getQuizId());
            quizzesResponse.setQuizTitle(lessonsDto.getQuizTitle());
            response.setQuizzesResponseList(List.of(quizzesResponse));
        } else {
            response.setQuizzesResponseList(null);
        }
        return response;
    }
}

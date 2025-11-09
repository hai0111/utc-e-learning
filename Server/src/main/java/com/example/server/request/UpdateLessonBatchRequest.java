package com.example.server.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UpdateLessonBatchRequest {
    @NotNull(message = "Lessons list is required")
    @Size(min = 1, message = "At least one lesson is required")
    @Valid
    private List<UpdateLessonItem> lessons;

    @Data
    @NoArgsConstructor
    public static class UpdateLessonItem {

        @NotNull(message = "Lesson ID is required")
        private String id;

        private Integer orderIndex;

        private Boolean isActive;
    }
}

package com.example.server.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


public class UpdateLessonBatchRequest {
    @NotNull(message = "Lessons list is required")
    @Size(min = 1, message = "At least one lesson is required")
    @Valid
    private List<UpdateLessonItem> lessons;

    // Getters and Setters
    public List<UpdateLessonItem> getLessons() { return lessons; }
    public void setLessons(List<UpdateLessonItem> lessons) { this.lessons = lessons; }

    public static class UpdateLessonItem {

        @NotNull(message = "Lesson ID is required")
        private String id;

        private Integer orderIndex;

        private Boolean isActive;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public Integer getOrderIndex() { return orderIndex; }
        public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }
}

package com.example.server.response;

import com.example.server.enums.LessonType;
import com.example.server.model.Users;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
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
    private Integer orderIndex;
    private UUID createdBy;
    private String createdByName;
    private UUID updatedBy;
    private String updatedByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}

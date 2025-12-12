package com.example.server.dto;

import java.util.Date;
import java.util.UUID;

public interface LessonsDto {

    UUID getId();

    UUID getCourseId();

    String getTitle();

    String getUrl();

    String getLessonType();

    Boolean getIsActive();

    Boolean getIsDelete();

    Integer getOrderIndex();

    UUID getCreatedById();

    String getCreatedByName();

    UUID getUpdatedById();

    String getUpdatedByName();

    Date getCreatedAt();

    Date getUpdatedAt();
}

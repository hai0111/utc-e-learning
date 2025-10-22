package com.example.server.dto;

import java.util.UUID;

public interface CoursesDto {

    UUID getId();

    String getTitle();

    String getDescription();

    String getInstructor();
}

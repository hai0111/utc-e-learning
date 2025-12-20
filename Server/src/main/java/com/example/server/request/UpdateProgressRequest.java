package com.example.server.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateProgressRequest {
    @NotNull(message = "Lesson ID cannot be null")
    private UUID lessonId;

    @NotNull(message = "Progress percentage cannot be null")
    @Min(value = 0, message = "Progress must be at least 0")
    @Max(value = 100, message = "Progress must be at most 100")
    private Double progressPercentage;
}

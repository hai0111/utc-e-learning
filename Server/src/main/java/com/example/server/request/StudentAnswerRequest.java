package com.example.server.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class StudentAnswerRequest {

    @NotNull(message = "Question ID is required")
    private UUID questionId;

    // List of selected answers
    // If question type is TEXT then list of null
    private List<UUID> selectedOptionIds;

    // For question type TEXT
    private String textAnswer;
}

package com.example.server.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizOptionsRequest {

    @NotBlank(message = "Option text is required")
    private String optionText;

    @NotNull(message = "Is correct status is required")
    private Boolean isCorrect;

    private Integer orderIndex;
}

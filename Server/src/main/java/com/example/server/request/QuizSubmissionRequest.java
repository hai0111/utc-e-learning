package com.example.server.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuizSubmissionRequest {

    @NotEmpty(message = "Answers cannot be empty")
    @Valid
    private List<StudentAnswerRequest> studentAnswers;
}

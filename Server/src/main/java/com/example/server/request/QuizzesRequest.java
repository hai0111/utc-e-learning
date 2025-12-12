package com.example.server.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class QuizzesRequest {

    @NotBlank(message = "Quiz title cannot be blank")
    private String quizTitle;

    @Valid
    private List<QuizQuestionsRequest> quizQuestionsRequests;
}

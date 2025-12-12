package com.example.server.request;

import com.example.server.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuizQuestionsRequest {

    @NotBlank(message = "Question text can not be blank")
    private String questionText;

    @NotNull(message = "Question type is required")
    private QuestionType questionType;

    @NotNull(message = "Raw is required")
    @Min(value = 0, message = "Point cannot be negative")
    private Double rawPoint;

    private Integer orderIndex;

    @Valid
    private List<QuizOptionsRequest> optionsRequestList;
}

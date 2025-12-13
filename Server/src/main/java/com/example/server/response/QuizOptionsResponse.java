package com.example.server.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizOptionsResponse {

    private String optionText;

    private Boolean isCorrect;

    private Integer orderIndex;
}

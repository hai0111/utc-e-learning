package com.example.server.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class QuizOptionsResponse {

    private UUID optionId;

    private String optionText;

    private Boolean isCorrect;

    private Integer orderIndex;
}

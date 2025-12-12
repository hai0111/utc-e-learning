package com.example.server.response;

import com.example.server.enums.QuestionType;
import com.example.server.model.QuizOptions;
import com.example.server.model.QuizQuestions;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class QuizQuestionResponse {

    private String questionText;

    private QuestionType questionType;

    private Double rawPoint;

    private Integer orderIndex;

    private List<QuizOptionsResponse> optionsResponseList = new ArrayList<>();

    public static List<QuizQuestionResponse> convertToResponse(List<QuizQuestions> quizQuestionsList) {
        List<QuizQuestionResponse> quizQuestionResponseList = new ArrayList<>();
        if (quizQuestionsList == null) {
            return quizQuestionResponseList;
        }
        for (QuizQuestions quizQuestions : quizQuestionsList) {
            QuizQuestionResponse response = new QuizQuestionResponse();
            response.setQuestionText(quizQuestions.getQuestionText());
            response.setQuestionType(quizQuestions.getQuestionType());
            response.setRawPoint(quizQuestions.getRawPoint());
            response.setOrderIndex(quizQuestions.getOrderIndex());
            if (quizQuestions.getOptions() != null) {
                for (QuizOptions quizOptions : quizQuestions.getOptions()) {
                    QuizOptionsResponse responseOptions = new QuizOptionsResponse();
                    responseOptions.setOptionText(quizOptions.getOptionText());
                    responseOptions.setIsCorrect(quizOptions.getIsCorrect());
                    responseOptions.setOrderIndex(quizOptions.getOrderIndex());
                    response.getOptionsResponseList().add(responseOptions);
                }
            }
            quizQuestionResponseList.add(response);
        }
        return quizQuestionResponseList;
    }
}

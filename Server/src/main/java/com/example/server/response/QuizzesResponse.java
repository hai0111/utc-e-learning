package com.example.server.response;

import com.example.server.model.Quizzes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuizzesResponse {

    private String quizTitle;

    private List<QuizQuestionResponse> quizQuestionsResponses;

    public static QuizzesResponse getQuizzesResponse(Quizzes quizzes) {
        QuizzesResponse response = new QuizzesResponse();
        response.setQuizTitle(quizzes.getTitle());
        if (quizzes.getQuizQuestions() != null) {
            response.setQuizQuestionsResponses(QuizQuestionResponse.convertToResponse(quizzes.getQuizQuestions()));
        } else {
            response.setQuizQuestionsResponses(List.of());
        }
        return response;
    }
}

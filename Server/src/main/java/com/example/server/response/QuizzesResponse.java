package com.example.server.response;

import com.example.server.model.Quizzes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class QuizzesResponse {

    private UUID quizId;

    private String quizTitle;

    private List<QuizQuestionResponse> quizQuestionsResponses;

    private Boolean isAttempted;

    public static QuizzesResponse getQuizzesResponse(Quizzes quizzes) {
        QuizzesResponse response = new QuizzesResponse();
        response.setQuizId(quizzes.getId());
        response.setQuizTitle(quizzes.getTitle());
        if (quizzes.getQuizQuestions() != null) {
            response.setQuizQuestionsResponses(QuizQuestionResponse.convertToResponse(quizzes.getQuizQuestions()));
        } else {
            response.setQuizQuestionsResponses(List.of());
        }
        response.setIsAttempted(false);
        return response;
    }
}

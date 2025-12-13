package com.example.server.service.impl;

import com.example.server.enums.QuestionType;
import com.example.server.model.QuizAnswers;
import com.example.server.model.QuizAttempts;
import com.example.server.model.QuizOptions;
import com.example.server.model.QuizQuestions;
import com.example.server.repository.*;
import com.example.server.response.OptionResponse;
import com.example.server.response.QuestionResultResponse;
import com.example.server.response.QuizAttemptDetailResponse;
import com.example.server.service.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizAttemptServiceImpl implements QuizAttemptService {

    private final QuizAttemptRepository attemptRepo;
    private final QuizQuestionsRepository questionRepo;
    private final QuizOptionsRepository optionRepo;
    private final QuizAnswerRepository answerRepo;

    @Override
    public QuizAttemptDetailResponse getAttemptResult(UUID attemptId) {
        // 1. Retrieve the attempt information
        QuizAttempts attempt = attemptRepo.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found with ID: " + attemptId));

        // 2. Get the Quiz ID from the relationship object
        // Model: 'private Quizzes quizzes;' -> Getter: 'getQuizzes()'
        UUID quizId = attempt.getQuizzes().getId();

        // 3. Retrieve list of questions for this Quiz
        // Model QuizQuestions uses: 'private Quizzes quizzes;' -> Repository uses: 'findByQuizzes_Id...'
        List<QuizQuestions> questions = questionRepo.findByQuizzes_IdOrderByOrderIndexAsc(quizId);

        List<UUID> questionIds = questions.stream()
                .map(QuizQuestions::getId)
                .collect(Collectors.toList());

        // 4. Bulk fetch Options and Answers
        // Model QuizOptions uses: 'private QuizQuestions quizQuestions;'
        List<QuizOptions> allOptions = optionRepo.findByQuizQuestions_IdIn(questionIds);

        // Model QuizAnswers uses: 'private QuizAttempts quizAttempts;'
        List<QuizAnswers> allAnswers = answerRepo.findByQuizAttempts_Id(attemptId);

        // 5. Group Data into Maps for performance (O(n))

        // Group Options by Question ID
        Map<UUID, List<QuizOptions>> optionsMap = allOptions.stream()
                .collect(Collectors.groupingBy(opt -> opt.getQuizQuestions().getId()));

        // Group Answers by Question ID
        // Model QuizAnswers uses: 'private QuizQuestions quizQuestions;'
        Map<UUID, List<QuizAnswers>> answersMap = allAnswers.stream()
                .collect(Collectors.groupingBy(ans -> ans.getQuizQuestions().getId()));

        // 6. Process logic for each question
        List<QuestionResultResponse> questionResults = new ArrayList<>();
        double totalScore = 0.0;
        boolean isPendingGrade = false;

        for (QuizQuestions question : questions) {
            List<QuizOptions> options = optionsMap.getOrDefault(question.getId(), new ArrayList<>());
            List<QuizAnswers> studentAnswers = answersMap.getOrDefault(question.getId(), new ArrayList<>());

            // Handle potential null rawPoint
            Double maxScore = (question.getRawPoint() != null) ? question.getRawPoint().doubleValue() : 0.0;

            QuestionResultResponse.QuestionResultResponseBuilder qBuilder = QuestionResultResponse.builder()
                    .questionId(question.getId())
                    .questionText(question.getQuestionText())
                    .type(question.getQuestionType().name())
                    .maxScore(maxScore);

            if (question.getQuestionType() == QuestionType.TEXT) {
                // --- ESSAY / TEXT ---
                if (!studentAnswers.isEmpty()) {
                    QuizAnswers ans = studentAnswers.get(0);
                    qBuilder.essayAnswer(ans.getRawText());

                    if (ans.getScore() == null) {
                        isPendingGrade = true;
                        qBuilder.studentScore(0.0);
                    } else {
                        qBuilder.studentScore(ans.getScore());
                        totalScore += ans.getScore();
                    }
                }
            } else {
                // --- MULTIPLE CHOICE ---

                // Map Options to DTO
                List<OptionResponse> optionDTOs = options.stream().map(opt -> OptionResponse.builder()
                        .id(opt.getId())
                        .text(opt.getOptionText())
                        .isCorrect(opt.getIsCorrect())
                        .build()).collect(Collectors.toList());
                qBuilder.options(optionDTOs);

                // Extract Selected Option IDs from QuizAnswers object
                List<UUID> selectedIds = studentAnswers.stream()
                        .map(QuizAnswers::getQuizOptions) // Get the QuizOptions object
                        .filter(Objects::nonNull)         // Safety check
                        .map(QuizOptions::getId)          // Get ID from QuizOptions
                        .collect(Collectors.toList());
                qBuilder.selectedOptionIds(selectedIds);

                // Calculate Score
                double qScore = studentAnswers.stream()
                        .map(QuizAnswers::getScore)
                        .filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .sum();

                qBuilder.studentScore(qScore);
                totalScore += qScore;
            }

            questionResults.add(qBuilder.build());
        }

        // 7. Build Final Response
        return QuizAttemptDetailResponse.builder()
                .id(attempt.getId())
                .totalScore(totalScore)
                .status(isPendingGrade ? "PENDING" : "GRADED")
                // Model field is 'feedBack', so Lombok generates 'getFeedBack()'
                .teacherFeedback(attempt.getFeedBack())
                .answers(questionResults)
                .build();
    }
}

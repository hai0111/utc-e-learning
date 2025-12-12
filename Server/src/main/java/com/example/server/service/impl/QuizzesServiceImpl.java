package com.example.server.service.impl;

import com.example.server.enums.QuestionType;
import com.example.server.enums.Role;
import com.example.server.exception.CustomServiceException;
import com.example.server.model.QuizOptions;
import com.example.server.model.QuizQuestions;
import com.example.server.model.Quizzes;
import com.example.server.model.Users;
import com.example.server.repository.QuizOptionsRepository;
import com.example.server.repository.QuizQuestionsRepository;
import com.example.server.repository.QuizzesRepository;
import com.example.server.repository.UsersRepository;
import com.example.server.request.QuizOptionsRequest;
import com.example.server.request.QuizQuestionsRequest;
import com.example.server.request.QuizzesRequest;
import com.example.server.security.service.UserDetailsImpl;
import com.example.server.service.QuizzesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class QuizzesServiceImpl implements QuizzesService {

    @Autowired
    private QuizzesRepository quizzesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private QuizQuestionsRepository quizQuestionsRepository;

    @Autowired
    private QuizOptionsRepository quizOptionsRepository;

    @Override
    @Transactional
    public Quizzes createQuizzes(QuizzesRequest quizzesRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Users user = usersRepository.findByIdAndIsActive(userDetails.getId(), true);
        if (user == null) {
            throw new CustomServiceException("User does not exist", HttpStatus.BAD_REQUEST);
        }
        if (user.getRole().equals(Role.STUDENT)) {
            throw new CustomServiceException("This user is not an instructor", HttpStatus.FORBIDDEN);
        }

        double totalScore = quizzesRequest.getQuizQuestionsRequests().stream()
                .mapToDouble(QuizQuestionsRequest::getRawPoint)
                .sum();

        if (Math.abs(totalScore - 100.0) > 0.001) {
            throw new CustomServiceException("Total score of all questions must be exactly 100. Current: " + totalScore, HttpStatus.BAD_REQUEST);
        }

        Quizzes quizzes = new Quizzes();
        quizzes.setTitle(quizzesRequest.getQuizTitle());
        quizzes.setCreatedAt(new Date());
        quizzes.setUpdatedAt(new Date());

        List<QuizQuestions> questionsRequestList = new ArrayList<>();
        for (QuizQuestionsRequest quizQuestionRequest: quizzesRequest.getQuizQuestionsRequests()) {
            validateQuestionLogic(quizQuestionRequest);
            QuizQuestions quizQuestion = new QuizQuestions();
            quizQuestion.setQuizzes(quizzes);
            quizQuestion.setQuestionText(quizQuestionRequest.getQuestionText());
            quizQuestion.setOrderIndex(quizQuestionRequest.getOrderIndex());
            quizQuestion.setQuestionType(quizQuestionRequest.getQuestionType());
            quizQuestion.setRawPoint(quizQuestionRequest.getRawPoint());
            quizQuestion.setCreatedAt(new Date());
            quizQuestion.setUpdatedAt(new Date());
            List<QuizOptions> optionsRequestList = List.of();
            if (quizQuestionRequest.getOptionsRequestList() != null) {
                optionsRequestList = new ArrayList<>();
                for (QuizOptionsRequest optionsRequest: quizQuestionRequest.getOptionsRequestList()) {
                    QuizOptions quizOptions = new QuizOptions();
                    quizOptions.setOptionText(optionsRequest.getOptionText());
                    quizOptions.setOrderIndex(optionsRequest.getOrderIndex());
                    quizOptions.setIsCorrect(optionsRequest.getIsCorrect());
                    quizOptions.setCreatedAt(new Date());
                    quizOptions.setUpdatedAt(new Date());
                    quizOptions.setQuizQuestions(quizQuestion);
                    optionsRequestList.add(quizOptions);
                }
            }
            quizQuestion.setOptions(optionsRequestList);
            questionsRequestList.add(quizQuestion);
        }
        quizzes.setQuizQuestions(questionsRequestList);
        return quizzesRepository.save(quizzes);
    }

    private void validateQuestionLogic(QuizQuestionsRequest request) {
        QuestionType type = request.getQuestionType();
        if (type == null) {
            throw new CustomServiceException("QuestionType is invalid or null. Allowed values: SINGLE, MULTIPLE, TEXT", HttpStatus.BAD_REQUEST);
        }

        List<QuizOptionsRequest> options = request.getOptionsRequestList();

        // If the question type is text => quizOption is null
        if (type == QuestionType.TEXT) {
            return;
        }

        // If the question type is multiple or single => there must be at least 2 options.
        if (options == null || options.size() < 2) {
            throw new CustomServiceException("Choice questions must have at least 2 options.", HttpStatus.BAD_REQUEST);
        }

        long correctCount = options.stream().filter(QuizOptionsRequest::getIsCorrect).count();

        if (type == QuestionType.SINGLE) {
            // If the question type is one => there is only one correct answer.
            if (correctCount != 1) {
                throw new CustomServiceException("Single choice question must have exactly one correct answer.", HttpStatus.BAD_REQUEST);
            }
        } else if (type == QuestionType.MULTIPLE) {
            // If there are multiple types of questions, there must be at least one correct answer.
            if (correctCount < 1) {
                throw new CustomServiceException("Multiple choice question must have at least one correct answer.", HttpStatus.BAD_REQUEST);
            }
        }
    }
}

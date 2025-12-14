package com.example.server.service.impl;

import com.example.server.enums.QuestionType;
import com.example.server.enums.Role;
import com.example.server.exception.CustomServiceException;
import com.example.server.model.QuizOptions;
import com.example.server.model.QuizQuestions;
import com.example.server.model.Quizzes;
import com.example.server.model.Users;
import com.example.server.repository.LessonRepository;
import com.example.server.repository.QuizAttemptsRepository;
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
import java.util.UUID;

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

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private QuizAttemptsRepository quizAttemptsRepository;

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

        List<QuizQuestions> questions = mapRequestToQuestionsAndOptions(quizzes, quizzesRequest.getQuizQuestionsRequests());
        quizzes.setQuizQuestions(questions);
        return quizzesRepository.save(quizzes);
    }

    @Override
    @Transactional
    public Quizzes updateQuizzes(Quizzes currentQuiz, QuizzesRequest quizzesRequest) {

        if (!currentQuiz.getTitle().equals(quizzesRequest.getQuizTitle())
                && quizzesRepository.existsByTitleAndIdNot(quizzesRequest.getQuizTitle(), currentQuiz.getId())) {
            throw new CustomServiceException("Quiz with this title already exists", HttpStatus.BAD_REQUEST);
        }

        // Check if this quiz is being shared with another lesson.
        long usageCount = lessonRepository.countByQuizzesId(currentQuiz.getId());
        boolean isShared = usageCount > 1;

        // Has any student already taken this quiz?
        boolean hasAttempts = quizAttemptsRepository.existsByQuizzesId(currentQuiz.getId());

        // If the quiz is shared -> Clone it into a new one (Separate lines)
        if (isShared) {
            return createQuizzes(quizzesRequest); // Call the create function again to create a new copy.
        }

        // If students have already taken the quiz -> Only edit the Title
        if (hasAttempts) {
            currentQuiz.setTitle(quizzesRequest.getQuizTitle());
            currentQuiz.setUpdatedAt(new Date());

            // Report an error if a quiz has already been completed by a student but you are still requesting edits.
            if (quizzesRequest.getQuizQuestionsRequests() != null && !quizzesRequest.getQuizQuestionsRequests().isEmpty()) {
                throw new CustomServiceException("The quiz has already been taken by students; only the title needs to be changed.", HttpStatus.BAD_REQUEST);
            }
            return quizzesRepository.save(currentQuiz);
        }

        // This quiz hasn't been taken by anyone yet and hasn't been assigned to any other lesson -> update quiz
        return updateExistingQuizStructure(currentQuiz, quizzesRequest);
    }

    @Override
    @Transactional
    public void deleteQuizIfUnused(UUID quizId) {
        if (quizId == null) return;

        // Check if this quiz is being shared with another lesson
        long usageCount = lessonRepository.countByQuizzesId(quizId);

        // Has any student already taken this quiz
        boolean hasAttempts = quizAttemptsRepository.existsByQuizzesId(quizId);

        // usageCount <= 0 means that no lesson points to it anymore (after the previous lesson was set to null)
        // Or usageCount <= 1 if the transaction has not committed to setting null
        if (usageCount <= 1 && !hasAttempts) {
            quizzesRepository.deleteById(quizId);
        }
    }

    private Quizzes updateExistingQuizStructure(Quizzes currentQuiz, QuizzesRequest request) {
        double totalScore = request.getQuizQuestionsRequests().stream()
                .mapToDouble(QuizQuestionsRequest::getRawPoint).sum();

        if (Math.abs(totalScore - 100.0) > 0.001) {
            throw new CustomServiceException("Total score of all questions must be exactly 100. Current: " + totalScore, HttpStatus.BAD_REQUEST);
        }

        currentQuiz.setTitle(request.getQuizTitle());
        currentQuiz.setUpdatedAt(new Date());

        // Delete question old
        currentQuiz.getQuizQuestions().clear();

        // Create new questions
        if (request.getQuizQuestionsRequests() != null) {
            List<QuizQuestions> newQuestions = mapRequestToQuestionsAndOptions(currentQuiz, request.getQuizQuestionsRequests());
            currentQuiz.getQuizQuestions().addAll(newQuestions);
        }

        return quizzesRepository.save(currentQuiz);
    }

    private List<QuizQuestions> mapRequestToQuestionsAndOptions(Quizzes quiz, List<QuizQuestionsRequest> requestList) {
        List<QuizQuestions> quizQuestionsList = new ArrayList<>();
        if (requestList == null) return quizQuestionsList;

        for (QuizQuestionsRequest quizQuestionRequest : requestList) {
            validateQuestionLogic(quizQuestionRequest);

            QuizQuestions question = new QuizQuestions();
            question.setQuizzes(quiz);
            question.setQuestionText(quizQuestionRequest.getQuestionText());
            question.setQuestionType(quizQuestionRequest.getQuestionType());
            question.setRawPoint(quizQuestionRequest.getRawPoint());
            question.setOrderIndex(quizQuestionRequest.getOrderIndex());
            question.setCreatedAt(new Date());
            question.setUpdatedAt(new Date());

            List<QuizOptions> options = new ArrayList<>();
            if (quizQuestionRequest.getQuestionType() != QuestionType.TEXT && quizQuestionRequest.getOptionsRequestList() != null) {
                for (QuizOptionsRequest oReq : quizQuestionRequest.getOptionsRequestList()) {
                    QuizOptions option = new QuizOptions();
                    option.setQuizQuestions(question);
                    option.setOptionText(oReq.getOptionText());
                    option.setIsCorrect(oReq.getIsCorrect());
                    option.setOrderIndex(oReq.getOrderIndex());
                    option.setCreatedAt(new Date());
                    option.setUpdatedAt(new Date());
                    options.add(option);
                }
            }
            question.setOptions(options);
            quizQuestionsList.add(question);
        }
        return quizQuestionsList;
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

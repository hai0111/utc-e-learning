package com.example.server.service.impl;

import com.example.server.enums.QuestionType;
import com.example.server.enums.Role;
import com.example.server.exception.CustomServiceException;
import com.example.server.model.Enrollment;
import com.example.server.model.Lessons;
import com.example.server.model.Progress;
import com.example.server.model.QuizAnswers;
import com.example.server.model.QuizAttempts;
import com.example.server.model.QuizOptions;
import com.example.server.model.QuizQuestions;
import com.example.server.model.Quizzes;
import com.example.server.model.Users;
import com.example.server.repository.EnrollmentRepository;
import com.example.server.repository.LessonRepository;
import com.example.server.repository.ProgressRepository;
import com.example.server.repository.QuizAnswersRepository;
import com.example.server.repository.QuizAttemptsRepository;
import com.example.server.repository.QuizOptionsRepository;
import com.example.server.repository.QuizQuestionsRepository;
import com.example.server.repository.QuizzesRepository;
import com.example.server.repository.UsersRepository;
import com.example.server.request.QuizOptionsRequest;
import com.example.server.request.QuizQuestionsRequest;
import com.example.server.request.QuizSubmissionRequest;
import com.example.server.request.QuizzesRequest;
import com.example.server.request.StudentAnswerRequest;
import com.example.server.response.ApiResponse;
import com.example.server.response.QuizSubmissionResponse;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private QuizAnswersRepository quizAnswersRepository;

    @Autowired
    private ProgressRepository progressRepository;

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

    @Override
    public ApiResponse<Object> submitQuizzes(QuizSubmissionRequest request, UUID quizId, UUID lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Lessons lessons = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CustomServiceException("Lesson not found", HttpStatus.NOT_FOUND));

        // Check if this quiz is correct for this lesson
        if (lessons.getQuizzes() == null || !lessons.getQuizzes().getId().equals(quizId)) {
            throw new CustomServiceException("This quiz does not belong to the specified lesson", HttpStatus.BAD_REQUEST);
        }

        Quizzes quiz = lessons.getQuizzes();

        // Check if this student has already registered for this course
        Enrollment enrollment = enrollmentRepository.findActiveEnrollmentByStudentAndCourse(userDetails.getId(), lessons.getCourse().getId())
                .orElseThrow(() -> new CustomServiceException("Student is not enrolled in this course", HttpStatus.FORBIDDEN));

        // Check how many times the student has taken this quiz
        Integer currentAttemptCount = quizAttemptsRepository.countByEnrollmentIdAndQuizzesId(enrollment.getId(), quizId);

        QuizAttempts attempt = new QuizAttempts();
        attempt.setEnrollment(enrollment);
        attempt.setQuizzes(quiz);
        attempt.setLessons(lessons);
        attempt.setAttemptNo(currentAttemptCount + 1);
        attempt.setCreatedAt(new Date());

        attempt = quizAttemptsRepository.save(attempt);

        double totalScoreObtained = 0.0;
        List<QuizAnswers> answersToSave = new ArrayList<>();

        // Flag is used to indicate whether there is an essay question
        boolean hasQuestionTypeText = false;

        // Retrieve all questions and correct answers from the database using quizId
        List<QuizQuestions> questionsList = quizQuestionsRepository.findAllByQuizzesId(quizId);
        Map<UUID, QuizQuestions> questionMap = questionsList.stream()
                .collect(Collectors.toMap(QuizQuestions::getId, questions -> questions));

        // Browse the list of STUDENT's answers
        for (StudentAnswerRequest answerRequest : request.getStudentAnswers()) {
            QuizQuestions question = questionMap.get(answerRequest.getQuestionId());
            if (question == null) continue; // Skip if the question ID does not belong to this quiz.

            double obtainedPointForQuestion = 0.0;

            // CASE A: (TEXT)
            // If it's an essay question, the score will be set to 0, and we'll wait for the instructor to grade it
            if (question.getQuestionType() == QuestionType.TEXT) {
                hasQuestionTypeText = true;

                QuizAnswers answer = new QuizAnswers();
                answer.setQuizAttempts(attempt);
                answer.setQuizQuestions(question);
                answer.setRawText(answerRequest.getTextAnswer());
                answer.setScore(0.0);
                answer.setCreatedAt(new Date());

                answersToSave.add(answer);
            }

            // CASE B: SINGLE / MULTIPLE
            else {
                // Get the list of correct answer IDs from the database
                Set<UUID> correctOptionIds = question.getOptions().stream()
                        .filter(QuizOptions::getIsCorrect)
                        .map(QuizOptions::getId)
                        .collect(Collectors.toSet());

                // Get the list of student answer IDs
                List<UUID> selectedIds = answerRequest.getSelectedOptionIds() != null ? answerRequest.getSelectedOptionIds() : new ArrayList<>();
                Set<UUID> selectedIdsSet = new HashSet<>(selectedIds);

                // Create flags to check if the student has selected all the correct answers for a question
                boolean isCorrect = false;

                if (question.getQuestionType() == QuestionType.SINGLE) {
                    // True if you choose one answer AND that answer is within the correct set
                    if (selectedIdsSet.size() == 1 && correctOptionIds.contains(selectedIds.get(0))) {
                        isCorrect = true;
                    }
                } else if (question.getQuestionType() == QuestionType.MULTIPLE) {
                    // True if: Number of selected answers == Number of correct answers and the list of selected answers matches the list of correct answers
                    // (Making even one mistake will result in losing all points for that question)
                    if (selectedIdsSet.size() == correctOptionIds.size() && correctOptionIds.containsAll(selectedIdsSet)) {
                        isCorrect = true;
                    }
                }

                if (isCorrect) {
                    obtainedPointForQuestion = question.getRawPoint();
                }

                totalScoreObtained += obtainedPointForQuestion;

                if (selectedIds.isEmpty()) {
                    // If the student doesn't select anything -> Save a blank record to indicate that the selection has been skipped
                    QuizAnswers emptyAns = new QuizAnswers();
                    emptyAns.setQuizAttempts(attempt);
                    emptyAns.setQuizQuestions(question);
                    emptyAns.setScore(0.0);
                    emptyAns.setQuizOptions(null);
                    emptyAns.setCreatedAt(new Date());
                    answersToSave.add(emptyAns);
                } else {
                    double scorePerOption = isCorrect ? (obtainedPointForQuestion / selectedIds.size()) : 0.0;
                    // Save each selected option.
                    for (UUID optId : selectedIds) {
                        QuizOptions optionEntity = quizOptionsRepository.findById(optId).orElse(null);
                        if (optionEntity != null) {
                            QuizAnswers ans = new QuizAnswers();
                            ans.setQuizAttempts(attempt);
                            ans.setQuizQuestions(question);
                            ans.setQuizOptions(optionEntity);
                            ans.setScore(scorePerOption);
                            ans.setCreatedAt(new Date());
                            answersToSave.add(ans);
                        }
                    }
                }
            }
        }

        quizAnswersRepository.saveAll(answersToSave);

//        attempt.setTotalScore(totalScoreObtained);

        quizAttemptsRepository.save(attempt);

        double maxScore = questionsList.stream().mapToDouble(QuizQuestions::getRawPoint).sum();
        double passThreshold = maxScore * 0.5; // Example: 50% is needed to pass the subject.

        if (totalScoreObtained >= passThreshold) {
            updateLessonProgress(enrollment, lessons);
        }

        String message = hasQuestionTypeText
                ? "Submission successful. The essay is awaiting grading by the instructor."
                : "Submission successful.";

        QuizSubmissionResponse quizSubmissionResponse = QuizSubmissionResponse.builder()
                .attemptId(attempt.getId())
                .totalScore(totalScoreObtained)
                .maxScore(maxScore)
                .statusMessage(message)
                .build();
        return new ApiResponse<>(200, "Submit quiz successfully", quizSubmissionResponse);
    }

    private void updateLessonProgress(Enrollment enrollment, Lessons lesson) {
        // Check if there is a progress record
        Progress progress = progressRepository.findByEnrollment_IdAndLessons_Id(enrollment.getId(), lesson.getId())
                .orElse(new Progress());

        // Update only if it's not 100% complete. If it's already finished, there's no need to update again
        if (progress.getProgressPercentage() == null || progress.getProgressPercentage() < 100.0) {
            progress.setEnrollment(enrollment);
            progress.setLessons(lesson);
            progress.setProgressPercentage(100.0); // Quiz completed and passed the minimum score -> 100%
            progress.setUpdatedAt(new Date());

            progressRepository.save(progress);
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

        List<Integer> questionIndexes = requestList.stream()
                .map(QuizQuestionsRequest::getOrderIndex)
                .toList();
        validateUniqueOrderIndex(questionIndexes, "Question");

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
                List<Integer> optionIndexes = quizQuestionRequest.getOptionsRequestList().stream()
                        .map(QuizOptionsRequest::getOrderIndex)
                        .toList();
                try {
                    validateUniqueOrderIndex(optionIndexes, "Option");
                } catch (CustomServiceException e) {
                    throw new CustomServiceException(
                            "Error in question '" + quizQuestionRequest.getQuestionText() + "': " + e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    );
                }
                for (QuizOptionsRequest quizOptionsRequest : quizQuestionRequest.getOptionsRequestList()) {
                    QuizOptions option = new QuizOptions();
                    option.setQuizQuestions(question);
                    option.setOptionText(quizOptionsRequest.getOptionText());
                    option.setIsCorrect(quizOptionsRequest.getIsCorrect());
                    option.setOrderIndex(quizOptionsRequest.getOrderIndex());
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

    private void validateUniqueOrderIndex(List<Integer> orderIndexes, String entityName) {
        Set<Integer> uniqueIndexes = new HashSet<>();
        for (Integer index : orderIndexes) {
            if (index == null) {
                throw new CustomServiceException(entityName + " order index cannot be null", HttpStatus.BAD_REQUEST);
            } // Ignore null if allowed.
            if (!uniqueIndexes.add(index)) {
                // If add returns false, it means the item already exists in Set -> Duplicate.
                throw new CustomServiceException("Duplicate " + entityName + " order index: " + index, HttpStatus.BAD_REQUEST);
            }
        }
    }
}

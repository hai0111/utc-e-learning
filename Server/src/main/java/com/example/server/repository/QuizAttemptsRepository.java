package com.example.server.repository;

import com.example.server.model.QuizAttempts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizAttemptsRepository extends JpaRepository<QuizAttempts, UUID> {
    List<QuizAttempts> findByEnrollment_Id(UUID enrollmentId);

    // If you need to check specific quiz history for a student
    List<QuizAttempts> findByEnrollment_IdAndQuizzes_Id(UUID enrollmentId, UUID quizId);

    // Check if anyone has already taken this quiz.
    boolean existsByQuizzesId(UUID quizId);

    // Count how many rows there are in the attempts table with this Enrollment ID and this Quiz ID
    Integer countByEnrollmentIdAndQuizzesId(UUID enrollmentId, UUID quizId);
}

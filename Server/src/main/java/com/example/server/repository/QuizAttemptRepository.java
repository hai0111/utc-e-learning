package com.example.server.repository;

import com.example.server.model.QuizAttempts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempts, UUID> {
    List<QuizAttempts> findByEnrollment_Id(UUID enrollmentId);

    // If you need to check specific quiz history for a student
    List<QuizAttempts> findByEnrollment_IdAndQuizzes_Id(UUID enrollmentId, UUID quizId);
}

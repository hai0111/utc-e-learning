package com.example.server.repository;

import com.example.server.model.QuizAnswers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizAnswersRepository extends JpaRepository<QuizAnswers, UUID> {
    // Get all answers submitted for a specific attempt
    List<QuizAnswers> findByQuizAttempts_Id(UUID attemptId);
}

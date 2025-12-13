package com.example.server.repository;

import com.example.server.model.QuizQuestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizQuestionsRepository extends JpaRepository<QuizQuestions, UUID> {
    // Get questions for a quiz, ordered by their sequence
    List<QuizQuestions> findByQuizzes_IdOrderByOrderIndexAsc(UUID quizId);
}

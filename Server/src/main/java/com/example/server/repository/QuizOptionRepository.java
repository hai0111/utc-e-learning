package com.example.server.repository;


import com.example.server.model.QuizOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizOptionRepository extends JpaRepository<QuizOptions, UUID> {
    // Fetch options for a list of questions (Bulk fetch)
    List<QuizOptions> findByQuizQuestions_IdIn(List<UUID> questionIds);
}

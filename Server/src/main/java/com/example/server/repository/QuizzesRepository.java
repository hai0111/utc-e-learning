package com.example.server.repository;

import com.example.server.model.Quizzes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuizzesRepository extends JpaRepository<Quizzes, UUID> {
}

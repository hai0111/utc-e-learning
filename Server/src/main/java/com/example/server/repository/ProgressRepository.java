package com.example.server.repository;

import com.example.server.model.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProgressRepository extends JpaRepository<Progress, UUID> {
    Optional<Progress> findByEnrollment_IdAndLessons_Id(UUID enrollmentId, UUID lessonId);
}

package com.example.server.repository;

import com.example.server.model.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProgressRepository extends JpaRepository<Progress, UUID> {
    @Query("SELECT COUNT(p) FROM Progress p " +
            "WHERE p.enrollment.course.id = :courseId " +
            "AND p.enrollment.users.id = :studentId " +
            "AND p.progressPercentage >= 100")
    long countCompletedLessonsByCourseAndStudent(@Param("courseId") UUID courseId,
                                                 @Param("studentId") UUID studentId);
}

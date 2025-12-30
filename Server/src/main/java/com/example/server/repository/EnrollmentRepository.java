package com.example.server.repository;

import com.example.server.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    @Query("SELECT e " +
            "FROM Enrollment e " +
            "WHERE e.users.id = :studentId AND e.course.id = :courseId and e.isActive = true")
    Enrollment findByStudentIdAndCourseId(UUID studentId, UUID courseId);

    @Query("SELECT e FROM Enrollment e " +
            "WHERE e.users.id = :studentId " +
            "AND e.course.id = :courseId " +
            "AND e.isActive = true")
    Optional<Enrollment> findActiveEnrollmentByStudentAndCourse(UUID studentId, UUID courseId);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Enrollment e " +
            "JOIN Lessons l ON l.course.id = e.course.id " +
            "WHERE e.users.id = :studentId " +
            "AND l.quizzes.id = :quizId " +
            "AND e.isActive = true")
    boolean existsByStudentIdAndQuizId(UUID studentId, UUID quizId);
}

package com.example.server.repository;

import com.example.server.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    @Query("SELECT e " + "FROM Enrollment e " + "WHERE e.users.id = :studentId AND e.course.id = :courseId and e.isActive = true")
    Enrollment findByStudentIdAndCourseId(UUID studentId, UUID courseId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.users.id = :instructorId")
    long countTotalStudentsByInstructor(@Param("instructorId") UUID instructorId);

    boolean existsByCourse_IdAndUsers_Id(UUID courseId, UUID userId);
}

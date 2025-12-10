package com.example.server.repository;

import com.example.server.model.Lessons;
import com.example.server.response.LessonResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lessons, UUID> {
    // For Admin - Get all lessons in a course
    @Query("SELECT new com.example.server.response.LessonResponse(" +
            "l.id, l.course.id, l.title, l.url, l.lessonType, l.isActive, l.isDelete, l.orderIndex, " +
            "l.createdBy.id, creator.name, l.updatedBy.id, updater.name, l.createdAt, l.updatedAt) " +
            "FROM Lessons l " +
            "LEFT JOIN l.createdBy creator " +
            "LEFT JOIN l.updatedBy updater " +
            "WHERE l.course.id = :courseId AND l.isDelete != true " +
            "ORDER BY l.orderIndex ASC")
    List<LessonResponse> findByCourseIdAndIsActiveTrueOrderByOrderIndexAsc(@Param("courseId") UUID courseId);

    // For Instructor - Get lessons in courses they own
    @Query("SELECT new com.example.server.response.LessonResponse(" +
            "l.id, l.course.id, l.title, l.url, l.lessonType, l.isActive, l.isDelete, l.orderIndex, " +
            "l.createdBy.id, creator.name, l.updatedBy.id, updater.name, l.createdAt, l.updatedAt) " +
            "FROM Lessons l " +
            "LEFT JOIN l.createdBy creator " +
            "LEFT JOIN l.updatedBy updater " +
            "WHERE l.course.id = :courseId " +
            "AND l.createdBy.id = :instructorId AND l.isDelete != true " +
            "ORDER BY l.orderIndex ASC")
    List<LessonResponse> findByCourseIdAndInstructorIdAndIsActiveTrueOrderByOrderIndexAsc(
            @Param("courseId") UUID courseId,
            @Param("instructorId") UUID instructorId);

    // For Student - Get lessons in courses they enrolled
    @Query("SELECT new com.example.server.response.LessonResponse(" +
            "l.id, l.course.id, l.title, l.url, l.lessonType, l.isActive, l.isDelete, l.orderIndex, " +
            "l.createdBy.id, creator.name, l.updatedBy.id, updater.name, l.createdAt, l.updatedAt) " +
            "FROM Lessons l " +
            "LEFT JOIN l.createdBy creator " +
            "LEFT JOIN l.updatedBy updater " +
            "WHERE l.course.id = :courseId AND l.isDelete != true AND l.isActive = true " +
            "AND l.course.id IN (SELECT e.course.id FROM Enrollment e WHERE e.users.id = :studentId) " +
            "ORDER BY l.orderIndex ASC")
    List<LessonResponse> findByCourseIdAndStudentIdAndIsActiveTrueOrderByOrderIndexAsc(
            @Param("courseId") UUID courseId,
            @Param("studentId") UUID studentId);

    // Get single lesson for Admin
    @Query("SELECT new com.example.server.response.LessonResponse(" +
            "l.id, l.course.id, l.title, l.url, l.lessonType, l.isActive, l.isDelete, l.orderIndex, " +
            "l.createdBy.id, creator.name, l.updatedBy.id, updater.name, l.createdAt, l.updatedAt) " +
            "FROM Lessons l " +
            "LEFT JOIN l.createdBy creator " +
            "LEFT JOIN l.updatedBy updater " +
            "WHERE l.id = :id AND l.course.id = :courseId AND l.isActive = true")
    Optional<LessonResponse> findByIdAndCourseIdAndIsActiveTrue(
            @Param("id") UUID id,
            @Param("courseId") UUID courseId);

    // Get single lesson for Instructor
    @Query("SELECT new com.example.server.response.LessonResponse(" +
            "l.id, l.course.id, l.title, l.url, l.lessonType, l.isActive, l.isDelete, l.orderIndex, " +
            "l.createdBy.id, creator.name, l.updatedBy.id, updater.name, l.createdAt, l.updatedAt) " +
            "FROM Lessons l " +
            "LEFT JOIN l.createdBy creator " +
            "LEFT JOIN l.updatedBy updater " +
            "WHERE l.id = :id AND l.course.id = :courseId AND l.isActive = true " +
            "AND l.createdBy.id = :instructorId")
    Optional<LessonResponse> findByIdAndCourseIdAndInstructorIdAndIsActiveTrue(
            @Param("id") UUID id,
            @Param("courseId") UUID courseId,
            @Param("instructorId") UUID instructorId);

    // Get single lesson for Student
    @Query("SELECT new com.example.server.response.LessonResponse(" +
            "l.id, l.course.id, l.title, l.url, l.lessonType, l.isActive, l.isDelete, l.orderIndex, " +
            "l.createdBy.id, creator.name, l.updatedBy.id, updater.name, l.createdAt, l.updatedAt) " +
            "FROM Lessons l " +
            "LEFT JOIN l.createdBy creator " +
            "LEFT JOIN l.updatedBy updater " +
            "WHERE l.id = :id AND l.course.id = :courseId AND l.isActive = true " +
            "AND l.course.id IN (SELECT e.course.id FROM Enrollment e WHERE e.users.id = :studentId)")
    Optional<LessonResponse> findByIdAndCourseIdAndStudentIdAndIsActiveTrue(
            @Param("id") UUID id,
            @Param("courseId") UUID courseId,
            @Param("studentId") UUID studentId);

    // Entity queries
    Optional<Lessons> findByIdAndCourseId(UUID id, UUID courseId);

    @Query("SELECT COUNT(l) > 0 FROM Lessons l WHERE l.course.id = :courseId AND l.title = :title AND l.isActive = true")
    boolean existsByCourseIdAndTitleAndIsActiveTrue(@Param("courseId") UUID courseId, @Param("title") String title);

    @Query("SELECT COUNT(l) > 0 FROM Lessons l WHERE l.course.id = :courseId AND l.title = :title AND l.id != :id AND l.isActive = true")
    boolean existsByCourseIdAndTitleAndIdNotAndIsActiveTrue(
            @Param("courseId") UUID courseId,
            @Param("title") String title,
            @Param("id") UUID id);

    @Query("SELECT COALESCE(MAX(l.orderIndex), 0) FROM Lessons l WHERE l.course.id = :courseId AND l.isActive = true")
    Integer findMaxOrderIndexByCourseId(@Param("courseId") UUID courseId);

    // Check if instructor owns the course
    @Query("SELECT COUNT(c) > 0 FROM Courses c WHERE c.id = :courseId AND c.users.id = :instructorId")
    boolean existsCourseByInstructorId(@Param("courseId") UUID courseId, @Param("instructorId") UUID instructorId);

    // Find lesson entity for update operations
    @Query("SELECT l FROM Lessons l WHERE l.id = :id AND l.course.id = :courseId AND l.isActive = true")
    Optional<Lessons> findLessonEntityByIdAndCourseIdAndIsActiveTrue(@Param("id") UUID id, @Param("courseId") UUID courseId);

    @Query("SELECT l FROM Lessons l WHERE l.id = :id AND l.course.id = :courseId AND l.isActive = true " +
            "AND l.course.users.id = :instructorId")
    Optional<Lessons> findLessonEntityByIdAndCourseIdAndInstructorIdAndIsActiveTrue(
            @Param("id") UUID id,
            @Param("courseId") UUID courseId,
            @Param("instructorId") UUID instructorId);

    // Method for find lesson by id and course id (No need isActive check)
    @Query("SELECT l FROM Lessons l WHERE l.id = :id AND l.course.id = :courseId")
    Optional<Lessons> findLessonEntityByIdAndCourseId(
            @Param("id") UUID id,
            @Param("courseId") UUID courseId);

    // Method for find lesson by id, course id and instructor id (No need isActive check)
    @Query("SELECT l FROM Lessons l WHERE l.id = :id AND l.course.id = :courseId AND l.course.users.id = :instructorId")
    Optional<Lessons> findLessonEntityByIdAndCourseIdAndInstructorId(
            @Param("id") UUID id,
            @Param("courseId") UUID courseId,
            @Param("instructorId") UUID instructorId);

    // Check duplicate orderIndex
    @Query("SELECT COUNT(l) > 0 FROM Lessons l WHERE l.course.id = :courseId AND l.orderIndex = :orderIndex AND l.id != :lessonId")
    boolean existsByCourseIdAndOrderIndexAndIdNot(
            @Param("courseId") UUID courseId,
            @Param("orderIndex") Integer orderIndex,
            @Param("lessonId") UUID lessonId);

    // Custom query with multiple search criteria
    @Query("SELECT l FROM Lessons l WHERE " +
            "LOWER(l.title) LIKE LOWER(CONCAT('%', :title, '%')) AND " +
            "l.course.id = :courseId")
    List<Lessons> searchLessonsByTitleAndCourse(
            @Param("title") String title,
            @Param("courseId") UUID courseId);
}

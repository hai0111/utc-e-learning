package com.example.server.repository;

import com.example.server.dto.LessonsDto;
import com.example.server.model.Lessons;
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
    @Query("SELECT l.id as id, l.course.id as courseId, l.title as title, l.url as url, " +
            "l.lessonType as lessonType, l.isActive as isActive, l.isDelete as isDelete, " +
            "l.orderIndex as orderIndex, " +
            "l.createdBy.id as createdById, creator.name as createdByName, " +
            "l.updatedBy.id as updatedById, updater.name as updatedByName, " +
            "l.createdAt as createdAt, l.updatedAt as updatedAt " +
            "FROM Lessons l " +
            "LEFT JOIN l.createdBy creator " +
            "LEFT JOIN l.updatedBy updater " +
            "WHERE l.course.id = :courseId AND l.isDelete != true " +
            "ORDER BY l.orderIndex ASC")
    List<LessonsDto> findByCourseIdAndIsActiveTrueOrderByOrderIndexAsc(@Param("courseId") UUID courseId);

    // For Instructor - Get lessons in courses they own
    @Query("SELECT l.id as id, l.course.id as courseId, l.title as title, l.url as url, " +
            "l.lessonType as lessonType, l.isActive as isActive, l.isDelete as isDelete, " +
            "l.orderIndex as orderIndex, " +
            "l.createdBy.id as createdById, creator.name as createdByName, " +
            "l.updatedBy.id as updatedById, updater.name as updatedByName, " +
            "l.createdAt as createdAt, l.updatedAt as updatedAt " +
            "FROM Lessons l " +
            "LEFT JOIN l.createdBy creator " +
            "LEFT JOIN l.updatedBy updater " +
            "WHERE l.course.id = :courseId " +
            "AND l.createdBy.id = :instructorId AND l.isDelete != true " +
            "ORDER BY l.orderIndex ASC")
    List<LessonsDto> findByCourseIdAndInstructorIdAndIsActiveTrueOrderByOrderIndexAsc(
            @Param("courseId") UUID courseId,
            @Param("instructorId") UUID instructorId);

    // For Student - Get lessons in courses they enrolled
    @Query("SELECT l.id as id, l.course.id as courseId, l.title as title, l.url as url, " +
            "l.lessonType as lessonType, l.isActive as isActive, l.isDelete as isDelete, " +
            "l.orderIndex as orderIndex, " +
            "l.createdBy.id as createdById, creator.name as createdByName, " +
            "l.updatedBy.id as updatedById, updater.name as updatedByName, " +
            "l.createdAt as createdAt, l.updatedAt as updatedAt " +
            "FROM Lessons l " +
            "LEFT JOIN l.createdBy creator " +
            "LEFT JOIN l.updatedBy updater " +
            "WHERE l.course.id = :courseId AND l.isDelete != true AND l.isActive = true " +
            "AND l.course.id IN (SELECT e.course.id FROM Enrollment e WHERE e.users.id = :studentId) " +
            "ORDER BY l.orderIndex ASC")
    List<LessonsDto> findByCourseIdAndStudentIdAndIsActiveTrueOrderByOrderIndexAsc(
            @Param("courseId") UUID courseId,
            @Param("studentId") UUID studentId);

    // Get single lesson for Admin
    @Query("SELECT l.id as id, l.course.id as courseId, l.title as title, l.url as url, " +
            "l.lessonType as lessonType, l.isActive as isActive, l.isDelete as isDelete, " +
            "l.orderIndex as orderIndex, " +
            "l.createdBy.id as createdById, creator.name as createdByName, " +
            "l.updatedBy.id as updatedById, updater.name as updatedByName, " +
            "l.createdAt as createdAt, l.updatedAt as updatedAt " +
            "FROM Lessons l " +
            "LEFT JOIN l.createdBy creator " +
            "LEFT JOIN l.updatedBy updater " +
            "WHERE l.id = :id AND l.course.id = :courseId AND l.isActive = true")
    Optional<LessonsDto> findByIdAndCourseIdAndIsActiveTrue(
            @Param("id") UUID id,
            @Param("courseId") UUID courseId);

    // Get single lesson for Instructor
    @Query("SELECT l.id as id, l.course.id as courseId, l.title as title, l.url as url, " +
            "l.lessonType as lessonType, l.isActive as isActive, l.isDelete as isDelete, " +
            "l.orderIndex as orderIndex, " +
            "l.createdBy.id as createdById, creator.name as createdByName, " +
            "l.updatedBy.id as updatedById, updater.name as updatedByName, " +
            "l.createdAt as createdAt, l.updatedAt as updatedAt " +
            "FROM Lessons l " +
            "LEFT JOIN l.createdBy creator " +
            "LEFT JOIN l.updatedBy updater " +
            "WHERE l.id = :id AND l.course.id = :courseId AND l.isActive = true " +
            "AND l.createdBy.id = :instructorId")
    Optional<LessonsDto> findByIdAndCourseIdAndInstructorIdAndIsActiveTrue(
            @Param("id") UUID id,
            @Param("courseId") UUID courseId,
            @Param("instructorId") UUID instructorId);

    // Get single lesson for Student
    @Query("SELECT l.id as id, l.course.id as courseId, l.title as title, l.url as url, " +
            "l.lessonType as lessonType, l.isActive as isActive, l.isDelete as isDelete, " +
            "l.orderIndex as orderIndex, " +
            "l.createdBy.id as createdById, creator.name as createdByName, " +
            "l.updatedBy.id as updatedById, updater.name as updatedByName, " +
            "l.createdAt as createdAt, l.updatedAt as updatedAt " +
            "FROM Lessons l " +
            "LEFT JOIN l.createdBy creator " +
            "LEFT JOIN l.updatedBy updater " +
            "WHERE l.id = :id AND l.course.id = :courseId AND l.isActive = true " +
            "AND l.course.id IN (SELECT e.course.id FROM Enrollment e WHERE e.users.id = :studentId)")
    Optional<LessonsDto> findByIdAndCourseIdAndStudentIdAndIsActiveTrue(
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

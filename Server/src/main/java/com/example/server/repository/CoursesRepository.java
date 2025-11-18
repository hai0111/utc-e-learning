package com.example.server.repository;

import com.example.server.dto.CoursesDto;
import com.example.server.model.Courses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CoursesRepository extends JpaRepository<Courses, UUID> {

    @Query("SELECT c.id as id, " +
            "c.title as title, " +
            "c.description as description, " +
            "c.isActive as isActive, " +
            "c.createdAt as createdAt, " +
            "c.updatedAt as updatedAt, " +
            "u.name as instructor " +
            "FROM Courses c " +
            "INNER JOIN Users u on c.users.id = u.id " +
            "WHERE c.isActive = true and c.users.id = :instructorId ORDER BY c.updatedAt DESC")
    Page<CoursesDto> findAllCourseByInstructor(Pageable pageable, UUID instructorId);

    @Query("SELECT c.id as id, " +
            "c.title as title, " +
            "c.description as description, " +
            "c.isActive as isActive, " +
            "c.createdAt as createdAt, " +
            "c.updatedAt as updatedAt, " +
            "u.name as instructor " +
            "FROM Courses c " +
            "INNER JOIN Users u on c.users.id = u.id " +
            "ORDER BY c.updatedAt DESC")
    Page<CoursesDto> findAllCourse(Pageable pageable);

    @Query("SELECT c.id as id, " +
            "c.title as title, " +
            "c.description as description, " +
            "c.isActive as isActive, " +
            "c.createdAt as createdAt, " +
            "c.updatedAt as updatedAt, " +
            "u.name as instructor " +
            "FROM Courses c " +
            "INNER JOIN Users u on c.users.id = u.id " +
            "INNER JOIN Enrollment e on e.course.id = c.id " +
            "WHERE c.isActive = true and e.users.id = :studentId ORDER BY c.updatedAt DESC")
    Page<CoursesDto> findAllCourseByStudent(Pageable pageable, UUID studentId);

    @Query("SELECT c.id as id, " +
            "c.title as title, " +
            "c.description as description, " +
            "c.isActive as isActive, " +
            "c.createdAt as createdAt, " +
            "c.updatedAt as updatedAt, " +
            "u.name as instructor " +
            "FROM Courses c " +
            "INNER JOIN Users u on c.users.id = u.id " +
            "WHERE c.id =:courseId ")
    CoursesDto findCourseByIdAndRoleAdminAndInstructor(UUID courseId);

    @Query("SELECT c.id as id, " +
            "c.title as title, " +
            "c.description as description, " +
            "c.isActive as isActive, " +
            "c.createdAt as createdAt, " +
            "c.updatedAt as updatedAt, " +
            "u.name as instructor " +
            "FROM Courses c " +
            "INNER JOIN Users u on c.users.id = u.id " +
            "WHERE c.id =:courseId and u.id = :instructorId and c.isActive = true")
    CoursesDto findCourseByIdAndInstructorId(UUID courseId, UUID instructorId);

    @Query("SELECT c.id as id, " +
            "c.title as title, " +
            "c.description as description, " +
            "c.isActive as isActive, " +
            "c.createdAt as createdAt, " +
            "c.updatedAt as updatedAt, " +
            "u.name as instructor, " +
            "SUM(p.progressPercentage) as sumProgressAchieved, " +
            "(SELECT COUNT(l.id) FROM Lessons l WHERE l.course.id = c.id) as totaLessonCount " +
            "FROM Courses c " +
            "INNER JOIN Users u on c.users.id = u.id " +
            "INNER JOIN Enrollment e on c.id = e.course.id " +
            "LEFT JOIN Progress p on p.enrollment.id = e.id " +
            "WHERE e.course.id =:courseId and e.users.id = :studentId and c.isActive = true " +
            "GROUP BY c.id, c.title, c.description, c.isActive, u.name")
    CoursesDto findCourseByIdAndStudentId(UUID courseId, UUID studentId);

    Courses findByIdAndIsActive(UUID courseId, Boolean isActive);

    @Query("SELECT c.id as id, " +
            "c.title as title, " +
            "c.description as description, " +
            "c.isActive as isActive, " +
            "c.createdAt as createdAt, " +
            "c.updatedAt as updatedAt, " +
            "u.name as instructor " +
            "FROM Courses c " +
            "INNER JOIN Users u on c.users.id = u.id " +
            "INNER JOIN Enrollment e on c.id = e.course.id " +
            "WHERE e.course.id =:courseId and e.users.id = :instructorId and c.isActive = true")
    Courses findByIdAndIsActiveAndInstructorId(UUID courseId, Boolean isActive, UUID instructorId);
}
